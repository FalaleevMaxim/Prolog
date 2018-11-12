package ru.prolog.compiler;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.TerminalNode;
import ru.prolog.PrologBaseListener;
import ru.prolog.PrologLexer;
import ru.prolog.PrologParser;
import ru.prolog.compiler.position.CodeInterval;
import ru.prolog.compiler.position.CodePos;
import ru.prolog.compiler.position.ModelCodeIntervals;
import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.NameModel;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.predicate.RuleExecutorPredicate;
import ru.prolog.model.program.Program;
import ru.prolog.model.program.ProgramModule;
import ru.prolog.model.rule.FactRule;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.model.storage.database.DatabaseModel;
import ru.prolog.model.storage.predicates.exceptions.SamePredicateException;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.model.type.Type;
import ru.prolog.model.type.descriptions.CompoundType;
import ru.prolog.model.type.descriptions.Functor;
import ru.prolog.model.type.descriptions.FunctorType;
import ru.prolog.model.values.*;
import ru.prolog.std.Not;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

public class PrologParseListener extends PrologBaseListener implements ANTLRErrorListener{
    private Program program;
    private Set<CompileException> exceptions = new HashSet<>();
    private List<IncludeStatement> includes;
    private boolean parseError = false;

    public PrologParseListener() {
    }

    private PrologParseListener(Program program) {
        this.program = program;
    }

    public Program getProgram() {
        if(!exceptions.isEmpty()) throw exceptions.iterator().next();
        return program;
    }

    @Override
    public void enterProgram(PrologParser.ProgramContext ctx) {
        if(parseError) throw new IllegalStateException("Errors during parsing");
        program = new Program();
    }

    @Override
    public void enterIncludes(PrologParser.IncludesContext ctx) {
        includes = new ArrayList<>(ctx.include().size());
    }

    @Override
    public void enterInclude(PrologParser.IncludeContext ctx) {
        Token baseToken;
        Include includeType;
        if(ctx.PREDICATE()!=null){
            includeType = Include.PREDICATE;
            baseToken = ctx.PREDICATE().getSymbol();
        }else {
            includeType = Include.MODULE;
            baseToken = ctx.MODULE().getSymbol();
        }
        String directory = ctx.directory.getText();
        directory = directory.substring(1, directory.length()-1);
        String className = ctx.className.getText();
        className = className.substring(1, className.length()-1);
        includes.add(new IncludeStatement(directory, className, includeType, new ModelCodeIntervals(
                tokenInterval(baseToken),
                fullInterval(ctx.getStart(), ctx.getStop()),
                Arrays.asList(tokenInterval(ctx.directory), tokenInterval(ctx.className)),
                ctx.LPAR().getSymbol().getStartIndex(),
                ctx.RPAR().getSymbol().getStartIndex())));
    }

    @Override
    public void enterTypedef(PrologParser.TypedefContext ctx) {
        if(parseError) throw new IllegalStateException("Errors during parsing");
        Type type = parseType(ctx.type());
        if(type==null) return;
        for (TerminalNode nameNode : ctx.NAME()){
            Token token = nameNode.getSymbol();
            String name = token.getText();
            program.domains().addType(name, type);
            program.domains().putNameModel(new NameModel(name, new ModelCodeIntervals(tokenInterval(token))));
        }
    }

    @Override
    public void exitDomain(PrologParser.DomainContext ctx) {
        processIncludes();
        program.domains().exceptions();
    }

    private Type parseType(PrologParser.TypeContext ctx){
        if(ctx.primitiveType!=null)
            return program.domains().get(ctx.primitiveType.getText());
        if(ctx.listOf!=null) {
            Type type = new Type(ctx.listOf.getText());
            type.setCodeIntervals(new ModelCodeIntervals(
                    tokenInterval(ctx.listOf.getStart()),
                    fullInterval(ctx.getStart(), ctx.getStop())));
            return type;
        }
        if(ctx.compoundType()!=null)
            return parseFunctorType(ctx.compoundType());
        exceptions.add(new CompileException(
                new CodeInterval(
                        new CodePos(ctx.getStart().getLine(), ctx.start.getCharPositionInLine()),
                        ctx.getStart().getStartIndex(),
                        ctx.getStop().getStopIndex()),
                "Can not read type description."));
        return null;
    }

    private Type parseFunctorType(PrologParser.CompoundTypeContext ctx){
        return new Type(
                new CompoundType(
                        ctx.functor().stream()
                                .map(this::parseFunctor)
                                .collect(Collectors.toList())));
    }

    private Functor parseFunctor(PrologParser.FunctorContext ctx){
        String name = ctx.NAME().getText();
        if(ctx.argTypes()==null) {
            FunctorType func = new FunctorType(name);
            if(ctx.LPAR()!=null){
                func.setCodeIntervals(new ModelCodeIntervals(
                        tokenInterval(ctx.NAME().getSymbol()),
                        fullInterval(ctx.getStart(), ctx.getStop()),
                        ctx.LPAR().getSymbol().getStartIndex(),
                        ctx.RPAR().getSymbol().getStartIndex()));
            }else {
                func.setCodeIntervals(new ModelCodeIntervals(tokenInterval(ctx.NAME().getSymbol())));
            }
            return func;
        }
        FunctorType func = new FunctorType(
                name,
                ctx.argTypes().typeName()
                        .stream().map(RuleContext::getText)
                        .collect(Collectors.toList()),
                program.domains());
        func.setCodeIntervals(new ModelCodeIntervals(
                tokenInterval(ctx.NAME().getSymbol()),
                fullInterval(ctx.getStart(), ctx.getStop()),
                ctx.argTypes().typeName().stream().map(ParserRuleContext::getStart).map(PrologParseListener::tokenInterval).collect(Collectors.toList()),
                ctx.LPAR().getSymbol().getStartIndex(),
                ctx.RPAR().getSymbol().getStartIndex()));
        return func;
    }

    @Override
    public void enterDatabase(PrologParser.DatabaseContext ctx) {
        if(parseError) throw new IllegalStateException("Errors during parsing");
        processIncludes();
        String name = DatabaseModel.DEFAULT_DB_NAME;
        if(ctx.NAME()!=null)
            name = ctx.NAME().getText();
            program.database().addDatabase(name);
        for(PrologParser.PredDefContext prCtx : ctx.predDef()){
            parseDatabasePredicate(prCtx, name);
        }
    }

    @SuppressWarnings("Duplicates")
    private void parseDatabasePredicate(PrologParser.PredDefContext ctx, String dbName){
        DatabasePredicate predicate;
        if(ctx.argTypes()!=null){
            predicate = new DatabasePredicate(
                    ctx.NAME().getText(),
                    args(ctx.argTypes()),
                    program.domains());
            predicate.setCodeIntervals(new ModelCodeIntervals(
                    tokenInterval(ctx.NAME().getSymbol()),
                    fullInterval(ctx.getStart(), ctx.getStop()),
                    ctx.argTypes().typeName().stream().map(ParserRuleContext::getStart).map(PrologParseListener::tokenInterval).collect(Collectors.toList()),
                    ctx.LPAR().getSymbol().getStartIndex(),
                    ctx.RPAR().getSymbol().getStartIndex()));
        }else{
            predicate = new DatabasePredicate(
                    ctx.NAME().getText(),
                    Collections.emptyList(),
                    program.domains());
            if(ctx.LPAR()!=null){
                predicate.setCodeIntervals(new ModelCodeIntervals(
                        tokenInterval(ctx.NAME().getSymbol()),
                        fullInterval(ctx.getStart(), ctx.getStop()),
                        ctx.LPAR().getSymbol().getStartIndex(),
                        ctx.RPAR().getSymbol().getStartIndex()));
            }else {
                predicate.setCodeIntervals(new ModelCodeIntervals(tokenInterval(ctx.NAME().getSymbol())));
            }
        }

        program.database().addPredicate(predicate, dbName);
        program.domains().addDatabasePredicate(predicate);
        program.predicates().add(predicate);
    }

    @Override
    public void enterPredicates(PrologParser.PredicatesContext ctx) {
        if(parseError) throw new IllegalStateException("Errors during parsing");
        processIncludes();
        ctx.predDef().forEach(this::parsePredicate);
    }

    private void parsePredicate(PrologParser.PredDefContext ctx){
        RuleExecutorPredicate predicate;
        if(ctx.argTypes()!=null){
            predicate = new RuleExecutorPredicate(
                    ctx.NAME().getText(),
                    args(ctx.argTypes()),
                    program.domains());
            predicate.setCodeIntervals(new ModelCodeIntervals(
                    tokenInterval(ctx.NAME().getSymbol()),
                    fullInterval(ctx.getStart(), ctx.getStop()),
                    ctx.argTypes().typeName().stream().map(ParserRuleContext::getStart).map(PrologParseListener::tokenInterval).collect(Collectors.toList()),
                    ctx.LPAR().getSymbol().getStartIndex(),
                    ctx.RPAR().getSymbol().getStartIndex()));
        }else{
            predicate = new RuleExecutorPredicate(
                    ctx.NAME().getText(),
                    Collections.emptyList(),
                    program.domains());
            if(ctx.LPAR()!=null){
                predicate.setCodeIntervals(new ModelCodeIntervals(
                        tokenInterval(ctx.NAME().getSymbol()),
                        fullInterval(ctx.getStart(), ctx.getStop()),
                        ctx.LPAR().getSymbol().getStartIndex(),
                        ctx.RPAR().getSymbol().getStartIndex()));
            }else {
                predicate.setCodeIntervals(new ModelCodeIntervals(tokenInterval(ctx.NAME().getSymbol())));
            }
        }
        try {
            program.predicates().add(predicate);
        }catch (SamePredicateException e){
            exceptions.add(e);
        }
    }

    private List<String> args(PrologParser.ArgTypesContext ctx){
        return ctx.typeName()
                .stream().map(RuleContext::getText)
                .collect(Collectors.toList());
    }

    private void processIncludes() {
        if(includes==null) return;
        for (IncludeStatement include : includes) {
            try {
                include.directory = include.directory.replace('\\', '/');
                if(!include.directory.endsWith(".jar") && !include.directory.endsWith("/"))
                    include.directory = include.directory + "/";
                ClassLoader loader = new URLClassLoader(new URL[]{new URL("file://"+include.directory)});
                Class<?> class_ = loader.loadClass(include.className);
                if(include.type==Include.PREDICATE){
                    Constructor<?> constructor = null;
                    boolean acceptsTypeStorage = false;
                    for (Constructor<?> c : class_.getConstructors()) {
                        if(c.getParameterCount()==0){
                            constructor = c;
                            break;
                        }
                        if(c.getParameterCount()==1 && c.getParameterTypes()[0].equals(TypeStorage.class)){
                            constructor = c;
                            acceptsTypeStorage = true;
                            break;
                        }
                    }
                    if(constructor==null){
                        exceptions.add(new CompileException(
                                include.codeIntervals.getArgs().get(1),
                                "Predicate class must contain empty constructor or constructor with only TypeStorage parameter"));
                        continue;
                    }
                    Object o = acceptsTypeStorage?
                            constructor.newInstance(program.domains()):
                            constructor.newInstance();
                    if(!(o instanceof Predicate)){
                        exceptions.add(new CompileException(
                                include.codeIntervals.getFullInterval(),
                                "Class does not implement Predicate"));
                        continue;
                    }
                    Predicate predicate = (Predicate) o;
                    predicate.setCodeIntervals(include.codeIntervals);
                    if(!predicate.exceptions().isEmpty()){
                        exceptions.addAll(predicate.exceptions());
                        continue;
                    }
                    predicate.fix();
                    program.predicates().add(predicate);
                }else {
                    Object o = class_.getConstructor().newInstance();
                    if(!(o instanceof ProgramModule)){
                        exceptions.add(new CompileException(
                                include.codeIntervals.getFullInterval(),
                                "Class does not implement ProgramModule"));
                        continue;
                    }
                    ProgramModule module = (ProgramModule) o;
                    if(!module.exceptions().isEmpty()){
                        exceptions.addAll(module.exceptions());
                        continue;
                    }
                    module.fix();
                    if(module.getTypeStorage()!=null){
                        program.domains().addTypes(module.getTypeStorage());
                    }
                    if(module.getPredicates()!=null){
                        for (Predicate p : module.getPredicates().all()) {
                            program.predicates().add(p);
                        }
                    }
                }
            } catch (MalformedURLException e) {
                exceptions.add(new CompileException(include.codeIntervals.getArgs().get(0), "Malformed directory/jar path", e));
            } catch (ClassNotFoundException e) {
                exceptions.add(new CompileException(include.codeIntervals.getArgs().get(1), "Class not found", e));
            } catch (NoSuchMethodException e) {
                if(include.type==Include.PREDICATE)
                    exceptions.add(new CompileException(
                            include.codeIntervals.getArgs().get(1),
                            "Predicate class must contain constructor with only TypeStorage parameter", e));
                else exceptions.add(new CompileException(
                        include.codeIntervals.getArgs().get(1),
                        "ProgramModule class must contain empty constructor", e));
            } catch (IllegalAccessException e) {
                exceptions.add(new CompileException(
                        include.codeIntervals.getArgs().get(1),
                        "Class constructor is not public", e));
            } catch (InstantiationException | InvocationTargetException e) {
                exceptions.add(new CompileException(
                        include.codeIntervals.getArgs().get(1),
                        "Error instantiating object", e));
            }
        }
        includes = null;
    }

    @Override
    public void exitProgram(PrologParser.ProgramContext ctx) {
        if(includes!=null) processIncludes();
    }

    @Override
    public void enterClauses(PrologParser.ClausesContext ctx) {
        if(parseError) throw new IllegalStateException("Errors during parsing");
        List<PrologParser.ClauseContext> clauses = ctx.clause();
        if(clauses ==null || clauses.isEmpty())
            return;
        clauses.forEach(this::parseClause);
    }

    @Override
    public void enterGoal(PrologParser.GoalContext ctx) {
        List<List<Statement>> body = parseRuleBody(ctx.ruleBody(), new VariableStorage());
        boolean firstList = true;
        for (List<Statement> list : body) {
            if(!firstList) program.goal().or();
            firstList = false;
            for (Statement st : list) {
                program.goal().addStatement(st);
            }
        }
    }

    private void parseClause(PrologParser.ClauseContext ctx){
        VariableStorage variables = new VariableStorage();
        String name = ctx.ruleLeft.NAME().getText();
        PrologParser.ArgListContext argList = ctx.ruleLeft.argList();
        int arity = 0;
        if(argList!=null && argList.value()!=null){
            arity = argList.value().size();
        }
        if(program.predicates().get(name).isEmpty()){
            exceptions.add(new CompileException(tokenInterval(ctx.ruleLeft.NAME().getSymbol()), "Predicate "+name+" does not exist"));
        }
        Predicate predicate = program.predicates().get(name, arity);
        List<Type> predTypes = predicate==null?
                        Collections.emptyList():
                        predicate.getArgTypes();
        List<ValueModel> toUnifyList = parseArgs(argList, variables, predTypes);
        if(predicate instanceof DatabasePredicate){
            FactRule fact = new FactRule(predicate, toUnifyList);
            if(ctx.ruleBody()!=null)
                exceptions.add(new ModelStateException(fact, "Predicate "+predicate+" is database predicate. Rule must be fact."));
            ((DatabasePredicate)predicate).addRule(fact);
            return;
        }
        StatementExecutorRule rule = new StatementExecutorRule(predicate, toUnifyList);
        rule.setCodeIntervals(new ModelCodeIntervals(
                new CodeInterval(
                        new CodePos(
                                ctx.ruleLeft.NAME().getSymbol().getLine(),
                                ctx.ruleLeft.NAME().getSymbol().getCharPositionInLine()
                        ),
                        ctx.ruleLeft.NAME().getSymbol().getStartIndex(),
                        ctx.ruleLeft.NAME().getSymbol().getStopIndex()),
                new CodeInterval(
                        new CodePos(
                                ctx.getStart().getLine(),
                                ctx.getStart().getCharPositionInLine()
                        ),
                        ctx.getStart().getStartIndex(),
                        ctx.getStop().getStopIndex())

        ));
        if(!(predicate instanceof RuleExecutorPredicate)) {
            exceptions.add(new ModelStateException(rule, "Predicate "+ predicate +" does not support rules"));
            return;
        }
        if(ctx.ruleBody()!=null){
            List<List<Statement>> body = parseRuleBody(ctx.ruleBody(), variables);
            boolean firstList = true;
            for (List<Statement> list : body) {
                if(!firstList) rule.or();
                firstList = false;
                for (Statement st : list) {
                    rule.addStatement(st);
                }
            }
        }
        ((RuleExecutorPredicate) predicate).addRule(rule);
    }

    private List<List<Statement>> parseRuleBody(PrologParser.RuleBodyContext ctx, VariableStorage variables){
        if(ctx.or()!=null){
            List<List<Statement>> statements = new ArrayList<>();
            statements.addAll(parseRuleBody(ctx.ruleBody(0), variables));
            statements.addAll(parseRuleBody(ctx.ruleBody(1), variables));
            return statements;
        }else {
            List<Statement> statements = new ArrayList<>(ctx.stat().size());
            for (PrologParser.StatContext stCtx : ctx.stat()) {
                statements.add(parseStat(stCtx, variables));
            }
            return Collections.singletonList(statements);
        }
    }

    private Statement parseStat(PrologParser.StatContext ctx, VariableStorage variables){
        if(ctx.cut()!=null){
            String name = ctx.cut().getText();
            Predicate cut = program.predicates().get(name, 0);
            Statement st = new Statement(cut, Collections.emptyList());
            st.setCodeIntervals(new ModelCodeIntervals(tokenInterval(ctx.cut().getStart())));
            return st;
        }
        if(ctx.predExec()!=null){
            return parsePredExec(ctx.predExec(), variables);
        }
        if(ctx.compare()!=null)
            return parseCompare(ctx.compare(), variables);
        if(ctx.not()!=null){
            Statement st = parsePredExec(ctx.not().predExec(), variables);
            st.setPredicate(new Not(st.getPredicate()));
            return st;
        }
        throw new IllegalArgumentException("Can not read statement");
    }

    private Statement parsePredExec(PrologParser.PredExecContext ctx, VariableStorage variables){
        String name = ctx.NAME().getText();
        if(ctx.argList()==null){
            Predicate p = program.predicates().getFitting(name, Collections.emptyList());
            if(p==null){
                Statement st = new Statement(name);
                st.setCodeIntervals(new ModelCodeIntervals(tokenInterval(ctx.NAME().getSymbol())));
                return st;
            }
            Statement st = new Statement(p, Collections.emptyList());
            ModelCodeIntervals intervals;
            if(ctx.LPAR()==null){
                intervals = new ModelCodeIntervals(tokenInterval(ctx.NAME().getSymbol()));
            } else{
                intervals = new ModelCodeIntervals(
                        tokenInterval(ctx.NAME().getSymbol()),
                        fullInterval(ctx.getStart(), ctx.getStop()),
                        ctx.LPAR().getSymbol().getStartIndex(),
                        ctx.RPAR().getSymbol().getStartIndex());
            }
            st.setCodeIntervals(intervals);
            return st;
        }

        int argCount = ctx.argList().value().size();
        if(program.predicates().get(name).isEmpty()){
            Statement st = new Statement(name);
            st.setCodeIntervals(new ModelCodeIntervals(tokenInterval(ctx.NAME().getSymbol())));
            return st;
        }
        Predicate predicate = program.predicates().get(name, argCount);
        if(predicate==null) predicate = program.predicates().getVarArgPredicate(name);

        List<ValueModel> args;
        if(predicate==null) args = parseArgs(ctx.argList(), variables, null);
        else args = parseArgs(ctx.argList(), variables, predicate.getArgTypes());

        List<Type> argTypes = args.stream().map(ValueModel::getType).collect(Collectors.toList());
        predicate = program.predicates().getFitting(name, argTypes);

        Statement st = new Statement(predicate, args);
        st.setCodeIntervals(new ModelCodeIntervals(
                tokenInterval(ctx.NAME().getSymbol()),
                fullInterval(ctx.getStart(), ctx.getStop()),
                ctx.LPAR().getSymbol().getStartIndex(),
                ctx.RPAR().getSymbol().getStartIndex()));
        return st;
    }

    private Statement parseCompare(PrologParser.CompareContext ctx, VariableStorage variables){
        String operator = ctx.operator.getText();
        ValueModel left = parseCompVal(ctx.left, variables, null);
        ValueModel right = parseCompVal(ctx.right, variables, left.getType());
        if(right.getType()!=null && !right.getType().equals(left.getType())) left = parseCompVal(ctx.left, variables, right.getType());
        if(left instanceof VariableModel &&
                right instanceof VariableModel &&
                left.getType()==null &&
                right.getType()==null) {
            variables.setRelated((VariableModel) left, (VariableModel) right);
        }
        Statement st = new Statement(
                program.predicates().get(operator,2),
                Arrays.asList(left, right));
        st.setCodeIntervals(new ModelCodeIntervals(tokenInterval(ctx.operator)));
        return st;
    }

    private ValueModel parseCompVal(PrologParser.CompValContext ctx, VariableStorage variables, Type expected){
        if(ctx.value()!=null) return parseValue(ctx.value(), variables, expected);
        return parseExpr(ctx.expr(), variables, expected);
    }

    private ExprValueModel parseExpr(PrologParser.ExprContext ctx, VariableStorage variables, Type expected){
        if(ctx.inner!=null){
            ExprValueModel expr = parseExpr(ctx.inner, variables, expected);
            expr.setCodeIntervals(new ModelCodeIntervals(
                    expr.getCodeIntervals().getBaseInterval(),
                    fullInterval(ctx.getStart(), ctx.getStop()),
                    ctx.LPAR().getSymbol().getStartIndex(),
                    ctx.RPAR().getSymbol().getStartIndex()));
            return expr;
        }
        if(ctx.negative()!=null) return parseNegative(ctx.negative(), variables, expected);
        if(ctx.funcExpr()!=null) return parseFuncExpr(ctx.funcExpr(), variables, expected);
        if(ctx.integer()!=null) return new ExprValueModel(parseIntegerValue(ctx.integer(), expected));
        if(ctx.real()!=null) return new ExprValueModel(parseRealValue(ctx.real()));
        if(ctx.VARNAME()!=null) return new ExprValueModel(parseVariable(ctx.VARNAME().getSymbol(), variables, expected));
        return parseBinaryExpr(ctx, variables, expected);
    }

    private ExprValueModel parseBinaryExpr(PrologParser.ExprContext ctx, VariableStorage variables, Type expected){
        String operator = ctx.operator.getText();
        if(operator.equals("div") || operator.equals("mod"))
            expected = program.domains().get("integer");
        ExprValueModel expr = new ExprValueModel(operator,
                parseExpr(ctx.left, variables, expected),
                parseExpr(ctx.right, variables, expected));
        expr.setCodeIntervals(new ModelCodeIntervals(
                tokenInterval(ctx.operator),
                fullInterval(ctx.getStart(), ctx.getStop())));
        return expr;
    }

    private ExprValueModel parseNegative(PrologParser.NegativeContext ctx, VariableStorage variables, Type expected){
        if(ctx.expr()!=null)
            return new ExprValueModel("-", parseExpr(ctx.expr(), variables, expected));
        if(ctx.VARNAME()!=null)
            return new ExprValueModel("-",
                    new ExprValueModel(
                            parseVariable(ctx.VARNAME().getSymbol(), variables, expected)));
        return new ExprValueModel("-", parseFuncExpr(ctx.funcExpr(), variables, expected));
    }

    private ExprValueModel parseFuncExpr(PrologParser.FuncExprContext ctx, VariableStorage variables, Type expected){
        String name = ctx.FUNCTION().getText();
        ExprValueModel func = new ExprValueModel(name, parseExpr(ctx.expr(), variables, expected));
        func.setCodeIntervals(new ModelCodeIntervals(
                tokenInterval(ctx.FUNCTION().getSymbol()),
                ctx.LPAR().getSymbol().getStartIndex(),
                ctx.RPAR().getSymbol().getStartIndex()));
        return func;
    }

    private List<ValueModel> parseArgs(PrologParser.ArgListContext ctx, VariableStorage variables, List<Type> expected){
        if(ctx==null || ctx.value()==null) return Collections.emptyList();
        List<ValueModel> args = new ArrayList<>(ctx.value().size());
        for (int i = 0; i < ctx.value().size(); i++) {
            if(expected!=null && i<expected.size()){
                args.add(parseValue(ctx.value().get(i), variables, expected.get(i)));
            }else args.add(parseValue(ctx.value().get(i), variables, null));
        }
        return args;
    }

    private ValueModel parseValue(PrologParser.ValueContext ctx, VariableStorage variables, Type expected){
        if (ctx.VARNAME()!=null){
            return parseVariable(ctx.VARNAME().getSymbol(), variables, expected);
        }
        if(ctx.integer()!=null){
            return parseIntegerValue(ctx.integer(), expected);
        }
        if(ctx.real()!=null){
            return parseRealValue(ctx.real());
        }
        if(ctx.CHAR()!=null){
            String cStr = ctx.CHAR().getText();
            cStr = cStr.substring(1,cStr.length()-1);
            char c;
            if(cStr.length()==1)
                c=cStr.charAt(0);
            else switch (cStr){
                case "\\n":
                    c='\n';
                    break;
                case "\\r":
                    c='\r';
                    break;
                case "\\t":
                    c='\t';
                    break;
                case "\\'":
                    c='\'';
                    break;
                case "\\\\":
                    c='\\';
                    break;
                default:
                    c = (char) Integer.parseInt(cStr.substring(2),16);
            }
            if(expected!=null && expected.isPrimitive() && expected.getPrimitiveType().isInteger())
                return new SimpleValueModel(expected, (int)c);
            return new SimpleValueModel(program.domains().get("char"), c);
        }
        if(ctx.STRING()!=null){
            return parseStringValue(ctx, expected);
        }
        if(ctx.symbol!=null){
            if(expected!=null && expected.isCompoundType()) {
                Functor func = program.domains().getFunctor(ctx.symbol.getText());
                if (func != null) {
                    return new FunctorValueModel(new Type(func.getCompoundType()), func.getName(), Collections.emptyList());
                }
            }
            return new SimpleValueModel(program.domains().get("symbol"), ctx.symbol.getText());
        }

        if(ctx.list()!=null){
            return parseList(ctx, variables, expected);
        }

        if(ctx.functorVal()!=null){
            return parseFunctorValue(ctx, variables);
        }

        throw new IllegalArgumentException("Unknown kind of value");
    }

    private ValueModel parseRealValue(PrologParser.RealContext ctx) {
        String str = ctx.REAL().getText();
        double value = Double.parseDouble(str);
        if(ctx.minus!=null){
            value = -value;
        }
        return new SimpleValueModel(program.domains().get("real"), value);
    }

    private ValueModel parseIntegerValue(PrologParser.IntegerContext ctx, Type expected) {
        String str = ctx.INTEGER().getText();
        int value;
        if(str.charAt(0)=='$'){
            str = str.substring(1);
            value = Integer.parseInt(str, 16);
        }else{
            value = Integer.parseInt(str);
        }
        if(ctx.minus!=null){
            value = -value;
        }
        if(expected!=null){
            if(expected.equals(program.domains().get("real")))
                return new SimpleValueModel(expected, (double)value);
            if(expected.equals(program.domains().get("char")) && value>0 && value<=Character.MAX_VALUE)
                return new SimpleValueModel(expected, (char)value);
        }
        SimpleValueModel val = new SimpleValueModel(program.domains().get("integer"), value);
        val.setCodeIntervals(new ModelCodeIntervals(fullInterval(ctx.getStart(), ctx.getStop())));
        return val;
    }

    private ValueModel parseStringValue(PrologParser.ValueContext ctx, Type expected) {
        String str = ctx.STRING().getText();
        str = str.substring(1, str.length()-1);
        StringBuilder sb = new StringBuilder();
        boolean backslash = false;
        char u = 0;
        char uPos = 0;
        boolean unicode = false;
        for(char c : str.toCharArray()) {
            //Reading 4 hex to unicode character
            if(unicode){
                u*=16;
                u+=Character.forDigit(c,16);
                if(uPos<3)
                    uPos++;
                else{
                    uPos=0;
                    unicode=false;
                    sb.append(u);
                    u=0;
                }
            }
            //If this or previous character is not backslash, just write it;
            if(c!='\\' && !backslash){
                sb.append(c);
                continue;
            }
            switch (c){
                case '\\':
                    if(backslash){
                        //If it is second backslash, write it
                        sb.append(c);
                        backslash = false;
                    }else {
                        backslash = true;
                    }
                    break;
                case '\"':
                    sb.append('\"');
                    backslash = false;
                    break;
                case 'n':
                    sb.append('\n');
                    backslash = false;
                    break;
                case 't':
                    sb.append('\t');
                    backslash = false;
                    break;
                case 'r':
                    sb.append('\r');
                    backslash = false;
                    break;
                case 'u':
                    unicode = true;
                    backslash = false;
                    break;
            }
        }

        ValueModel val;
        if(expected!=null && expected.equals(program.domains().get("symbol"))) {
            val = new SimpleValueModel(expected, sb.toString());
        }else {
            val = new SimpleValueModel(program.domains().get("string"), sb.toString());
        }
        val.setCodeIntervals(new ModelCodeIntervals(tokenInterval(ctx.STRING().getSymbol())));
        return val;
    }

    private ValueModel parseFunctorValue(PrologParser.ValueContext ctx, VariableStorage variables) {
        String name = ctx.functorVal().NAME().getText();
        Functor func = program.domains().getFunctor(name);
        List<Type> argTypes;
        if(func==null) argTypes = Collections.emptyList();
        else argTypes = func.getArgTypes();
        List<ValueModel> args = parseArgs(ctx.functorVal().argList(), variables, argTypes);
        if(func==null){
            func = new FunctorType(name,
                    args.stream()
                            .map(ValueModel::getType)
                            .map(type -> {
                                Iterator<String> it = program.domains().names(type).iterator();
                                return it.hasNext()?it.next():null;
                            }).collect(Collectors.toList()),
                    program.domains());
            new CompoundType(Collections.singletonList(func));
        }
        FunctorValueModel val = new FunctorValueModel(new Type(func.getCompoundType()), func.getName(), args);
        val.setCodeIntervals(new ModelCodeIntervals(
                tokenInterval(ctx.functorVal().NAME().getSymbol()),
                fullInterval(ctx.getStart(), ctx.getStop()),
                ctx.functorVal().LPAR().getSymbol().getStartIndex(),
                ctx.functorVal().RPAR().getSymbol().getStartIndex()));
        return val;
    }

    private ValueModel parseList(PrologParser.ValueContext ctx, VariableStorage variables, Type expected) {
        List<ValueModel> elements = new ArrayList<>();
        PrologParser.ListValuesContext listValuesContext = ctx.list().listValues();
        Type elType = null;
        if(expected!=null && expected.isList())
            elType = expected.getListType();

        if(listValuesContext !=null){
            for (PrologParser.ValueContext elementContext : listValuesContext.value()) {
                ValueModel element;
                if(expected!=null && expected.isList()){
                    element = parseValue(elementContext, variables, elType);
                }else{
                    element = parseValue(elementContext, variables, null);
                    if(elType==null && element.getType()!=null){
                        elType = element.getType();
                        //When element type is known reparse previous with expected type.
                        for (int i = 0; i < elements.size()-1; i++) {
                            elements.set(i, parseValue(elementContext, variables, elType));
                        }
                    }
                }
                elements.add(element);
            }
        }

        Type listType;
        if(expected!=null && expected.isList()){
            listType = expected;
        }else{
            listType = getListType(elType);
        }
        VariableModel tail = null;
        if(ctx.list().tail!=null){
            tail = parseVariable(ctx.list().tail, variables, listType);
            //If list element type unknown and tail type is known, set types for elements
            if(listType==null && tail.getType()!=null && tail.getType().isList()){
                listType = tail.getType();
                elType = listType.getListType();
                if(listValuesContext != null) {
                    final Type elTypeFinal = elType;
                    elements = listValuesContext.value().stream().map(elCtx -> parseValue(elCtx, variables, elTypeFinal)).collect(Collectors.toList());
                }
            }
        }

        ListValueModel list;
        if(tail==null) list = new ListValueModel(listType, elements);
        else list = new ListValueModel(listType, elements, tail);
        list.setCodeIntervals(new ModelCodeIntervals(
                fullInterval(ctx.getStart(), ctx.getStop()),
                ctx.list().LSQ().getSymbol().getStartIndex(),
                ctx.list().RSQ().getSymbol().getStartIndex()));
        return list;
    }

    private Type getListType(Type elType) {
        Type listType = null;
        if(elType!=null) {
            String elTypeName = null;
            if(elType.isPrimitive())
                elTypeName = elType.getPrimitiveType().getName();
            else{
                Collection<String> names = program.domains().names(elType);
                if(names!=null && !names.isEmpty()) elTypeName = names.iterator().next();
            }
            if(elTypeName!=null) listType = new Type(elType, elTypeName);
        }
        return listType;
    }

    private VariableModel parseVariable(Token variable, VariableStorage variables, Type expected) {
        VariableModel var = new VariableModel(variable.getText());
        if(expected!=null) var.setType(expected);
        variables.put(var);
        var.setCodeIntervals(new ModelCodeIntervals(tokenInterval(variable)));
        return var;
    }

    public Collection<CompileException> exceptions() {
        return Collections.unmodifiableSet(exceptions);
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        if(e==null) exceptions.add(new CompileException(new CodePos(line, charPositionInLine), msg));
        else exceptions.add(new CompileException(new CodePos(line, charPositionInLine), msg, e));
        parseError = true;
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
    }

    private static CodeInterval tokenInterval(Token token){
        return new CodeInterval(
                new CodePos(
                        token.getLine(),
                        token.getCharPositionInLine()
                ),
                token.getStartIndex(),
                token.getStopIndex());
    }

    private static CodeInterval fullInterval(Token first, Token last){
        return new CodeInterval(
                new CodePos(
                        first.getLine(),
                        first.getCharPositionInLine()
                ),
                first.getStartIndex(),
                last.getStopIndex());
    }

    public static StatementExecutorRule parseOuterGoal(Program program, String goalStr, Collection<CompileException> exceptions){
        if(!program.exceptions().isEmpty()){
            exceptions.addAll(program.exceptions());
            return null;
        }
        CharStream input = CharStreams.fromString(goalStr);
        PrologLexer lexer = new PrologLexer(input);
        TokenStream tokens = new BufferedTokenStream(lexer);
        PrologParser parser = new PrologParser(tokens);
        PrologParseListener compiler = new PrologParseListener(program);
        parser.removeErrorListeners();
        parser.addErrorListener(compiler);
        lexer.removeErrorListeners();
        lexer.addErrorListener(compiler);
        PrologParser.OuterGoalContext goalCtx = parser.outerGoal();
        if(!compiler.exceptions().isEmpty()){
            exceptions.addAll(compiler.exceptions());
            return null;
        }

        List<List<Statement>> ruleBody = compiler.parseRuleBody(goalCtx.ruleBody(), new VariableStorage());
        if(!compiler.exceptions().isEmpty()){
            exceptions.addAll(compiler.exceptions());
            return null;
        }

        StatementExecutorRule goal = new StatementExecutorRule(program.getGoalPredicate(), Collections.emptyList());
        for (int i = 0; i < ruleBody.size(); i++) {
            if(i>0) goal.or();
            for (Statement st : ruleBody.get(i)) {
                goal.addStatement(st);
            }
        }

        if(!goal.exceptions().isEmpty()){
            exceptions.addAll(goal.exceptions());
            return null;
        }

        return goal;
    }

    public static List<FactRule> parseDbFile(Program program, String dbFile, Collection<CompileException> exceptions) throws IOException {
        if(!program.exceptions().isEmpty()){
            exceptions.addAll(program.exceptions());
            return null;
        }
        CharStream input = CharStreams.fromFileName(dbFile);
        PrologLexer lexer = new PrologLexer(input);
        TokenStream tokens = new BufferedTokenStream(lexer);
        PrologParser parser = new PrologParser(tokens);
        PrologParseListener compiler = new PrologParseListener(program);
        parser.removeErrorListeners();
        parser.addErrorListener(compiler);
        lexer.removeErrorListeners();
        lexer.addErrorListener(compiler);
        PrologParser.ConsultContext consultCtx = parser.consult();
        if(!compiler.exceptions().isEmpty()){
            exceptions.addAll(compiler.exceptions());
            return null;
        }

        List<FactRule> facts = new ArrayList<>(consultCtx.predExec().size());
        for (PrologParser.PredExecContext factCtx : consultCtx.predExec()) {
            String name = factCtx.NAME().getText();
            DatabasePredicate predicate = program.database().getPredicate(name);
            if(predicate==null){
                exceptions.add(
                        new CompileException(
                                tokenInterval(factCtx.NAME().getSymbol()),
                                "Database predicate "+name+" not found"));
                continue;
            }
            FactRule fact;
            if(factCtx.argList()==null){
                fact = new FactRule(predicate, Collections.emptyList());
                if(factCtx.LPAR()==null){
                    //If rule without args does not have parentheses, in only contains of name.
                    fact.setCodeIntervals(new ModelCodeIntervals(tokenInterval(factCtx.NAME().getSymbol())));
                }else{
                    //For rule written with empty parentheses
                    fact.setCodeIntervals(new ModelCodeIntervals(
                            tokenInterval(factCtx.NAME().getSymbol()),
                            fullInterval(factCtx.getStart(), factCtx.getStop()),
                            factCtx.LPAR().getSymbol().getStartIndex(),
                            factCtx.RPAR().getSymbol().getStartIndex()));
                }
            }else{
                fact = new FactRule(predicate, compiler.parseArgs(factCtx.argList(), new VariableStorage(), predicate.getArgTypes()));
                fact.setCodeIntervals(new ModelCodeIntervals(
                        tokenInterval(factCtx.NAME().getSymbol()),
                        fullInterval(factCtx.getStart(), factCtx.getStop()),
                        factCtx.LPAR().getSymbol().getStartIndex(),
                        factCtx.RPAR().getSymbol().getStartIndex()));
            }
            Collection<ModelStateException> factExceptions = fact.exceptions();
            if(factExceptions.isEmpty()){
                fact.fix();
                facts.add(fact);
            }else{
                exceptions.addAll(factExceptions);
            }
        }

        if(!compiler.exceptions().isEmpty()){
            exceptions.addAll(compiler.exceptions());
        }

        if(!exceptions.isEmpty()) return null;
        return facts;
    }

    private static class VariableStorage{
        Map<String, List<VariableModel>> variables = new HashMap<>();
        Map<String, Type> types = new HashMap<>();
        Map<String, Set<VariableModel>> related;

        public void put(VariableModel var){
            if (var.getName().equals("_")) return;
            //If storage contains type for variable, sets it (no matter if variable already has type)
            if(types.containsKey(var.getName())){
                var.setType(types.get(var.getName()));
            }else if(var.getType()!=null){//Otherwise, if variable has type, it sets that type to all variables with same name
                types.put(var.getName(), var.getType());
                if(variables.containsKey(var.getName())){
                    variables.get(var.getName()).forEach(v -> v.setType(var.getType()));
                }
                //If variable is related to other, set type for related
                if(related!=null && related.containsKey(var.getName())){
                    Set<VariableModel> vars = related.get(var.getName());
                    vars.forEach(v->v.setType(var.getType()));
                    //Now, when types are set, there is no sense to keep related variables
                    vars.forEach(v->related.remove(v.getName()));

                    //Set type to all clones of related variables
                    vars.forEach(this::put);
                }
            }
            //Add variable to map.
            if(variables.containsKey(var.getName())){
                variables.get(var.getName()).add(var);
            }else{
                List<VariableModel> lst = new ArrayList<>();
                lst.add(var);
                variables.put(var.getName(), lst);
            }
        }

        @SuppressWarnings("Duplicates")
        public void setRelated(VariableModel v1, VariableModel v2){
            if(v1.getName().equals("_") || v2.getName().equals("_")) return;
            if(related==null) related = new HashMap<>();
            if(related.containsKey(v1.getName()) && related.containsKey(v2.getName())){
                Set<VariableModel> v1Set = related.get(v1.getName());
                Set<VariableModel> v2Set = related.get(v2.getName());
                if(v1Set==v2Set) return;
                v1Set.addAll(v2Set);
                related.remove(v2.getName());
                return;
            }
            if(related.containsKey(v1.getName())){
                Set<VariableModel> v1Related = related.get(v1.getName());
                v1Related.add(v2);
                related.put(v2.getName(), v1Related);
                return;
            }
            if(related.containsKey(v2.getName())){
                Set<VariableModel> v2Related = related.get(v1.getName());
                v2Related.add(v1);
                related.put(v1.getName(), v2Related);
                return;
            }
            Set<VariableModel> vars = new HashSet<>();
            vars.add(v1);
            vars.add(v2);
            related.put(v1.getName(), vars);
            related.put(v2.getName(), vars);
        }
    }

    private enum Include{
        PREDICATE,
        MODULE
    }

    private static class IncludeStatement{
        String directory, className;
        Include type;
        ModelCodeIntervals codeIntervals;

        public IncludeStatement(String directory, String className, Include type, ModelCodeIntervals intervals) {
            this.directory = directory;
            this.className = className;
            this.type = type;
            this.codeIntervals = intervals;
        }
    }
}
