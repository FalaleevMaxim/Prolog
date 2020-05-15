package ru.prolog.syntaxmodel.tree.semantics;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.storage.predicates.PredicateStorage;
import ru.prolog.model.storage.predicates.PredicateStorageImpl;
import ru.prolog.model.storage.type.TypeStorageImpl;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.nodes.*;
import ru.prolog.syntaxmodel.tree.nodes.modules.DatabaseNode;
import ru.prolog.syntaxmodel.tree.nodes.modules.ProgramNode;
import ru.prolog.syntaxmodel.tree.semantics.attributes.ToDeclaration;
import ru.prolog.syntaxmodel.tree.semantics.attributes.ToImplementations;
import ru.prolog.syntaxmodel.tree.semantics.attributes.ToUsages;
import ru.prolog.syntaxmodel.tree.semantics.attributes.errors.DeclarationNotFoundError;
import ru.prolog.syntaxmodel.tree.semantics.attributes.errors.DuplicateError;
import ru.prolog.syntaxmodel.tree.semantics.attributes.errors.IllegalPredicateError;
import ru.prolog.syntaxmodel.tree.semantics.attributes.errors.NoImplementationsError;

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
        if(programNode.getClauses() == null) return;
        List<FunctorNode> predicateCalls = programNode.getClauses().getRules().stream()
                .map(RuleNode::getStatementsSets).flatMap(Collection::stream)
                .map(StatementsSetNode::getStatements).flatMap(Collection::stream)
                .filter(StatementNode::isPredicateExec)
                .map(StatementNode::getPredicateExec)
                .collect(Collectors.toList());
        if(programNode.getGoal() != null) {
            programNode.getGoal().getStatementsSets().stream()
                    .map(StatementsSetNode::getStatements)
                    .flatMap(Collection::stream)
                    .filter(StatementNode::isPredicateExec)
                    .map(StatementNode::getPredicateExec)
                    .forEach(predicateCalls::add);
        }
        List<FunctorDefNode> allPredicates = getAllPredicates();
        for (FunctorNode call : predicateCalls) {
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
}
