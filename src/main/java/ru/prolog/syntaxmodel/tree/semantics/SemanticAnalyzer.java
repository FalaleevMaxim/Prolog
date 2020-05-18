package ru.prolog.syntaxmodel.tree.semantics;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.storage.predicates.PredicateStorage;
import ru.prolog.model.storage.predicates.PredicateStorageImpl;
import ru.prolog.model.storage.type.TypeStorageImpl;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.nodes.*;
import ru.prolog.syntaxmodel.tree.nodes.modules.DatabaseNode;
import ru.prolog.syntaxmodel.tree.nodes.modules.ProgramNode;
import ru.prolog.syntaxmodel.tree.semantics.attributes.*;
import ru.prolog.syntaxmodel.tree.semantics.attributes.errors.*;

import java.util.*;
import java.util.stream.Collectors;

public class SemanticAnalyzer {
    private static final PredicateStorage predicateStorage = new PredicateStorageImpl(new TypeStorageImpl());

    private final ProgramNode root;

    /**
     * Объявления предикатов
     */
    private List<FunctorDefNode> predicates;

    /**
     * Объявления предикатов
     */
    private List<FunctorDefNode> databasePredicates;

    /**
     * Объявления функторов внутри составных типов данных
     */
    private List<FunctorDefNode> functors;

    /**
     * Значения-функторы в правилах и цели
     */
    List<FunctorNode> functorUsages;

    /**
     * Вызовы предикатов
     */
    private Map<String, List<FunctorNode>> predicateCalls;

    public SemanticAnalyzer(ProgramNode root) {
        this.root = root;
    }

    /**
     * Производит семантический анализ программы, расставляя узлам семантические атрибуты и связи
     */
    public void performSemanticAnalysis() {
        indexFunctors(root);
        indexPredicates(root);
        analyzeRules(root);
        analyzePredicateCalls(root);
        analyzeUsages();
        analyzeFunctorUsages(root);
    }

    private void analyzeUsages() {
        for (FunctorDefNode predicate : predicates) {
            if (predicate.getSemanticInfo().getAttribute(ToImplementations.class).getImplementations().isEmpty()) {
                predicate.getSemanticInfo().putAttribute(new NoImplementationsError("No rules for predicate"));
            }
        }
    }

    private void indexFunctors(ProgramNode programNode) {
        if(programNode.getDomains() == null) return;
        functors = programNode.getDomains().getTypeDefNodes().stream()
                .filter(TypeDefNode::isCompound)
                .map(TypeDefNode::getCompoundType)
                .map(CompoundTypeNode::getFunctors)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        for (FunctorDefNode functor : functors) {
            functor.getSemanticInfo().putAttribute(new ToUsages());
            functor.getName().getSemanticInfo().putAttribute(new NameOf(functor));
        }
        checkUniqueFunctors();
    }

    private void checkUniqueFunctors() {
        Map<String, FunctorDefNode> functorNames = new HashMap<>();
        for (FunctorDefNode functor : functors) {
            String name = functor.getName().getText().toLowerCase();
            if (functorNames.containsKey(name)) {
                functor.getSemanticInfo().putAttribute(new DuplicateError("Another functor exists with same name"));
            } else {
                functorNames.put(name, functor);
            }
        }
    }

    private void indexPredicates(ProgramNode programNode) {
        predicates = new ArrayList<>();
        databasePredicates = new ArrayList<>();
        if(programNode.getPredicates() != null) predicates.addAll(programNode.getPredicates().getPredicates());
        programNode.getDatabases().stream().map(DatabaseNode::getPredicates).forEach(databasePredicates::addAll);
        List<FunctorDefNode> allPredicates = getAllPredicates();
        for (FunctorDefNode predicate : allPredicates) {
            predicate.getSemanticInfo().putAttribute(new ToImplementations());
            predicate.getSemanticInfo().putAttribute(new ToUsages());
            predicate.getName().getSemanticInfo().putAttribute(new NameOf(predicate));
        }
        checkUniquePredicates();
        checkUniqueDatabasePredicates();
        checkBuiltInPredicates(allPredicates);
    }

    public List<FunctorDefNode> getAllPredicates() {
        List<FunctorDefNode> allPredicates = new ArrayList<>(predicates);
        allPredicates.addAll(databasePredicates);
        return allPredicates;
    }

    public List<FunctorDefNode> getPredicates() {
        return Collections.unmodifiableList(predicates);
    }

    public List<FunctorDefNode> getDatabasePredicates() {
        return Collections.unmodifiableList(databasePredicates);
    }

    public List<FunctorDefNode> getFunctors() {
        return Collections.unmodifiableList(functors);
    }

    private void checkUniquePredicates() {
        Map<String, Map<Integer, FunctorDefNode>> predicateSignatures = new HashMap<>();
        for (FunctorDefNode predicate : predicates) {
            String name = predicate.getName().getText().toLowerCase();
            Map<Integer, FunctorDefNode> arities;
            if (predicateSignatures.containsKey(name)) {
                arities = predicateSignatures.get(name);
            } else {
                arities = new HashMap<>();
                predicateSignatures.put(name, arities);
            }
            int arity = predicate.getArgTypes().size();
            if(arities.containsKey(arity)) {
                predicate.getSemanticInfo().putAttribute(new DuplicateError("Another predicate with same name and arity exists"));
                arities.get(arity).getSemanticInfo().putAttribute(new DuplicateError("Another predicate with same name and arity exists"));
            } else {
                arities.put(arity, predicate);
            }
        }
    }

    private void checkUniqueDatabasePredicates() {
        Set<String> predicateNames = predicates.stream()
                .map(FunctorDefNode::getName)
                .map(Token::getText)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        Set<String> functorNames = functors.stream().map(FunctorDefNode::getName).map(Token::getText).collect(Collectors.toSet());
        Map<String, FunctorDefNode> dbPredicatesNames = new HashMap<>();
        for (FunctorDefNode databasePredicate : databasePredicates) {
            String name = databasePredicate.getName().getText().toLowerCase();
            if(dbPredicatesNames.containsKey(name)) {
                databasePredicate.getSemanticInfo().putAttribute(new DuplicateError("Another database predicate with same name exists"));
                dbPredicatesNames.get(name).getSemanticInfo().putAttribute(new DuplicateError("Another database predicate with same name exists"));
            } else {
                dbPredicatesNames.put(name, databasePredicate);
                if(predicateNames.contains(name)) {
                    databasePredicate.getSemanticInfo().putAttribute(new DuplicateError("Another predicate with same name exists"));
                } else if(functorNames.contains(name)) {
                    databasePredicate.getSemanticInfo().putAttribute(new DuplicateError("Functor with same name exists"));
                }
            }
        }
    }

    private void checkBuiltInPredicates(List<FunctorDefNode> allPredicates) {
        Set<String> builtInPredicatesNames = predicateStorage.all().stream()
                .map(Predicate::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        for (FunctorDefNode predicate : allPredicates) {
            String name = predicate.getName().getText().toLowerCase();
            if(builtInPredicatesNames.contains(name)) {
                predicate.getSemanticInfo().putAttribute(new IllegalPredicateError(String.format("Predicate %s is built in", name)));
            }
        }
    }

    private void analyzeRules(ProgramNode programNode) {
        if(programNode.getClauses() == null) return;
        for (RuleNode rule : programNode.getClauses().getRules()) {
            FunctorNode left = rule.getLeft();
            left.getName().getSemanticInfo().putAttribute(new NameOf(left));
            String name = left.getName().getText().toLowerCase();
            int arity = left.getArgs().size();
            Optional<FunctorDefNode> predicate = getAllPredicates().stream()
                    .filter(p -> name.equals(p.getName().getText().toLowerCase()))
                    .filter(p -> p.getArgTypes().size() == arity)
                    .findFirst();
            if(predicate.isPresent()) {
                left.getSemanticInfo().putAttribute(new ToDeclaration(predicate.get()));
                predicate.get().getSemanticInfo().getAttribute(ToImplementations.class).addImplementation(left);
            } else {
                if(predicateStorage.get(name, arity) == null && predicateStorage.getVarArgPredicate(name) == null) {
                    left.getSemanticInfo().putAttribute(new DeclarationNotFoundError(String.format(
                            "Not found predicate with name %s and %d arguments", name, arity)));
                }
            }
        }
    }

    private void analyzePredicateCalls(ProgramNode programNode) {
        List<FunctorNode> predicateCalls = new ArrayList<>();
        if(programNode.getClauses() != null) {
            List<StatementNode> statements = programNode.getClauses().getRules().stream()
                    .map(RuleNode::getStatementsSets).flatMap(Collection::stream)
                    .map(StatementsSetNode::getStatements).flatMap(Collection::stream)
                    .collect(Collectors.toList());
            predicateCalls.addAll(extractPredicateCalls(statements));
        }
        if(programNode.getGoal() != null) {
            List<StatementNode> statements = programNode.getGoal().getStatementsSets().stream()
                    .map(StatementsSetNode::getStatements)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            predicateCalls.addAll(extractPredicateCalls(statements));
        }
        List<FunctorDefNode> allPredicates = getAllPredicates();
        for (FunctorNode call : predicateCalls) {
            call.getName().getSemanticInfo().putAttribute(new NameOf(call));
            String name = call.getName().getText().toLowerCase();
            int arity = call.getArgs().size();
            if(predicateStorage.get(name, arity) != null || predicateStorage.getVarArgPredicate(name) != null) break;
            Optional<FunctorDefNode> predicate = allPredicates.stream()
                    .filter(p -> name.equals(p.getName().getText().toLowerCase()))
                    .filter(p -> arity == p.getArgTypes().size())
                    .findFirst();
            if(predicate.isPresent()) {
                call.getSemanticInfo().putAttribute(new ToDeclaration(predicate.get()));
                predicate.get().getSemanticInfo().getAttribute(ToUsages.class).addUsage(call);
            } else {
                call.getSemanticInfo().putAttribute(new DeclarationNotFoundError(String.format(
                        "Not found predicate with name %s and %d arguments", name, arity)));
            }
        }
    }

    private List<FunctorNode> extractPredicateCalls(List<StatementNode> statements) {
        List<FunctorNode> calls = statements.stream()
                .filter(StatementNode::isPredicateExec)
                .map(StatementNode::getPredicateExec)
                .collect(Collectors.toList());
        statements.stream()
                .filter(StatementNode::isPredicateExecNegation)
                .map(StatementNode::getPredicateExecNegation)
                .map(NotPredicateExec::getPredicateExec)
                .filter(Objects::nonNull)
                .forEach(calls::add);
        return calls;
    }

    private void analyzeFunctorUsages(ProgramNode programNode) {
        List<FunctorNode> usages = new ArrayList<>();
        if(programNode.getClauses() != null) {
            List<RuleNode> rules = programNode.getClauses().getRules();
            //Достаём все значения-функторы из левых частей правил
            rules.stream().map(RuleNode::getLeft)
                    .map(FunctorNode::getArgs)
                    .flatMap(Collection::stream)
                    .map(ValueNode::getInnerValues)
                    .flatMap(Collection::stream)
                    .filter(ValueNode::isFunctor)
                    .map(ValueNode::getFunctor)
                    .forEach(usages::add);
            //Достаём все выражения правых частей правил
            List<StatementNode> statements = rules.stream()
                    .map(RuleNode::getStatementsSets).flatMap(Collection::stream)
                    .map(StatementsSetNode::getStatements).flatMap(Collection::stream)
                    .collect(Collectors.toList());
            extractFunctorsFromStatements(statements, usages);
        }
        if(programNode.getGoal() != null) {
            List<StatementNode> statements = programNode.getGoal().getStatementsSets().stream()
                    .map(StatementsSetNode::getStatements).flatMap(Collection::stream)
                    .collect(Collectors.toList());
            extractFunctorsFromStatements(statements, usages);
        }
        functorUsages = usages;
        for (FunctorNode usage : usages) {
            usage.getName().getSemanticInfo().putAttribute(new NameOf(usage));
            Optional<FunctorDefNode> declaration = functors.stream()
                    .filter(def -> def.getName().getText().equals(usage.getName().getText()))
                    .findFirst();
            if(declaration.isPresent()) {
                //Соединяем использование функтора с объявлением
                SemanticInfo usageSemanticInfo = usage.getSemanticInfo();
                usageSemanticInfo.putAttribute(new ToDeclaration(declaration.get()));
                declaration.get().getSemanticInfo().getAttribute(ToUsages.class).addUsage(usage);

                //Проверяем количество аргументов
                int usageArgs = usage.getArgs().size();
                int declarationArgs = declaration.get().getArgTypes().size();
                if(usageArgs != declarationArgs) {
                    usageSemanticInfo.putAttribute(new WrongFunctorArgsCountError(
                            String.format("Wrong number of arguments. Expected %d, got %d",
                            declarationArgs, usageArgs)));
                }
            } else {
                if (usage.getLb() == null) {
                    //Если не найдено объявление функтора, и у функтора нет скобок, то это символьное значение
                    usage.getName().getSemanticInfo().putAttribute(new SymbolValue());
                } else {
                    //Если у функтора есть скобки, а объявление не найдено, добавляем ошибку
                    usage.getSemanticInfo().putAttribute(new DeclarationNotFoundError(
                            String.format("Functor declaration %s not found in domains", usage.getName().getText())));
                }
            }
        }
    }

    private void extractFunctorsFromStatements(List<StatementNode> statements, List<FunctorNode> usages) {
        //Достаём все значения-функторы из вызовов предикатов
        statements.stream()
                .filter(StatementNode::isPredicateExec)
                .map(StatementNode::getPredicateExec)
                .map(FunctorNode::getArgs)
                .flatMap(Collection::stream)
                .map(ValueNode::getInnerValues).flatMap(Collection::stream)
                .filter(ValueNode::isFunctor)
                .map(ValueNode::getFunctor)
                .forEach(usages::add);
        //Достаём все значения-функторы из отрицаний вызовов предикатов
        statements.stream()
                .filter(StatementNode::isPredicateExecNegation)
                .map(StatementNode::getPredicateExecNegation)
                .map(NotPredicateExec::getPredicateExec)
                .filter(Objects::nonNull)
                .map(FunctorNode::getArgs)
                .flatMap(Collection::stream)
                .map(ValueNode::getInnerValues).flatMap(Collection::stream)
                .filter(ValueNode::isFunctor)
                .map(ValueNode::getFunctor)
                .forEach(usages::add);
        //Собираем выражения-сравнения
        List<CompareNode> compareStatements = statements.stream()
                .filter(StatementNode::isCompareStatement)
                .map(StatementNode::getCompareStatement)
                .collect(Collectors.toList());
        //Выбираем все значения-функторы из левых частей выражений
        compareStatements.stream()
                .map(CompareNode::getLeft)
                .filter(ExprOrValueNode::isValue)
                .map(ExprOrValueNode::getValue)
                .map(ValueNode::getInnerValues).flatMap(Collection::stream)
                .filter(ValueNode::isFunctor)
                .map(ValueNode::getFunctor)
                .forEach(usages::add);
        //Выбираем все значения-функторы из правых частей выражений
        compareStatements.stream()
                .map(CompareNode::getRight)
                .filter(ExprOrValueNode::isValue)
                .map(ExprOrValueNode::getValue)
                .map(ValueNode::getInnerValues).flatMap(Collection::stream)
                .filter(ValueNode::isFunctor)
                .map(ValueNode::getFunctor)
                .forEach(usages::add);
    }
}
