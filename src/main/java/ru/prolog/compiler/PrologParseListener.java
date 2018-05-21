package ru.prolog.compiler;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import ru.prolog.PrologBaseListener;
import ru.prolog.PrologLexer;
import ru.prolog.PrologParser;
import ru.prolog.logic.model.NameModel;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.predicate.DatabasePredicate;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.predicate.RuleExecutorPredicate;
import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.model.rule.FactRule;
import ru.prolog.logic.model.rule.Statement;
import ru.prolog.logic.model.rule.StatementExecutorRule;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.type.descriptions.CompoundType;
import ru.prolog.logic.model.type.descriptions.Functor;
import ru.prolog.logic.model.type.descriptions.FunctorType;
import ru.prolog.logic.model.values.*;
import ru.prolog.logic.std.Not;
import ru.prolog.logic.storage.database.DatabaseModel;
import ru.prolog.compiler.position.CodeInterval;
import ru.prolog.compiler.position.CodePos;
import ru.prolog.compiler.position.ModelCodeIntervals;

import java.util.*;
import java.util.stream.Collectors;

public class PrologParseListener extends PrologBaseListener implements ANTLRErrorListener{
    private Program program;
    private Set<CompileException> exceptions = new HashSet<>();
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
            return new FunctorType(name);
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
                ctx.argTypes().typeName().stream().map(ParserRuleContext::getStart).map(this::tokenInterval).collect(Collectors.toList()),
                ctx.LPAR().getSymbol().getStartIndex(),
                ctx.RPAR().getSymbol().getStartIndex()));
        return func;
    }

    @Override
    public void enterDatabase(PrologParser.DatabaseContext ctx) {
        if(parseError) throw new IllegalStateException("Errors during parsing");
        String name = DatabaseModel.DEFAULT_DB_NAME;
        if(ctx.NAME()!=null)
            name = ctx.NAME().getText();
            program.database().addDatabase(name);
        for(PrologParser.PredDefContext prCtx : ctx.predDef()){
            parseDatabasePredicate(prCtx, name);
        }
    }

    private void parseDatabasePredicate(PrologParser.PredDefContext ctx, String dbName){
        DatabasePredicate predicate = new DatabasePredicate(
                ctx.NAME().getText(),
                args(ctx.argTypes()),
                program.domains());

        predicate.setCodeIntervals(new ModelCodeIntervals(
                tokenInterval(ctx.NAME().getSymbol()),
                fullInterval(ctx.getStart(), ctx.getStop()),
                ctx.argTypes().typeName().stream().map(ParserRuleContext::getStart).map(this::tokenInterval).collect(Collectors.toList()),
                ctx.LPAR().getSymbol().getStartIndex(),
                ctx.RPAR().getSymbol().getStartIndex()
        ));

        program.database().addPredicate(predicate, dbName);
        program.domains().addDatabasePredicate(predicate);
        program.predicates().add(predicate);
    }

    @Override
    public void enterPredicates(PrologParser.PredicatesContext ctx) {
        if(parseError) throw new IllegalStateException("Errors during parsing");
        ctx.predDef().forEach(this::parsePredicate);
    }

    private void parsePredicate(PrologParser.PredDefContext ctx){
        RuleExecutorPredicate predicate = new RuleExecutorPredicate(
                ctx.NAME().getText(),
                args(ctx.argTypes()),
                program.domains());

        predicate.setCodeIntervals(new ModelCodeIntervals(
                tokenInterval(ctx.NAME().getSymbol()),
                fullInterval(ctx.getStart(), ctx.getStop()),
                ctx.argTypes().typeName().stream().map(ParserRuleContext::getStart).map(this::tokenInterval).collect(Collectors.toList()),
                ctx.LPAR().getSymbol().getStartIndex(),
                ctx.RPAR().getSymbol().getStartIndex()
        ));

        program.predicates().add(predicate);
    }

    private List<String> args(PrologParser.ArgTypesContext ctx){
        return ctx.typeName()
                .stream().map(RuleContext::getText)
                .collect(Collectors.toList());
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
        if(left.getType()==null && right.getType()!=null) left = parseCompVal(ctx.left, variables, right.getType());
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
            if(i<expected.size()){
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
            if(expected.equals(program.domains().get("char")))
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
                    if(elType==null) elType = element.getType();
                }
                elements.add(element);
            }
        }

        Type listType = null;
        if(expected!=null && expected.isList()){
            listType = expected;
        }else{
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
        }
        VariableModel tail = null;
        if(ctx.list().tail!=null){
            tail = parseVariable(ctx.list().tail, variables, listType);
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

    private CodeInterval tokenInterval(Token token){
        return new CodeInterval(
                new CodePos(
                        token.getLine(),
                        token.getCharPositionInLine()
                ),
                token.getStartIndex(),
                token.getStopIndex());
    }

    private CodeInterval fullInterval(Token first, Token last){
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

    private static class VariableStorage{
        Map<String, List<VariableModel>> variables = new HashMap<>();
        Map<String, Type> types = new HashMap<>();

        public void put(VariableModel var){
            //If storage contains type for variable, sets it (no matter if variable already has type)
            if(types.containsKey(var.getName())){
                var.setType(types.get(var.getName()));
            }else if(var.getType()!=null){//Otherwise, if variable has type, it sets that type to all variables with same name
                types.put(var.getName(), var.getType());
                if(variables.containsKey(var.getName())){
                    variables.get(var.getName()).forEach(v -> v.setType(var.getType()));
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
    }
}
