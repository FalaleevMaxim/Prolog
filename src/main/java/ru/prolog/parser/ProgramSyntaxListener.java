package ru.prolog.parser;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import ru.prolog.PrologBaseListener;
import ru.prolog.PrologParser;
import ru.prolog.logic.model.predicate.DatabasePredicate;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.predicate.RuleExecutorPredicate;
import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.type.descriptions.CompoundType;
import ru.prolog.logic.model.type.descriptions.Functor;
import ru.prolog.logic.model.type.descriptions.FunctorType;
import ru.prolog.logic.storage.database.DatabaseModel;
import ru.prolog.logic.values.Variable;
import ru.prolog.logic.values.model.ValueModel;
import ru.prolog.logic.values.model.VariableModel;
import ru.prolog.logic.values.simple.SimpleValueModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProgramSyntaxListener extends PrologBaseListener{
    private Program program = new Program();

    public Program getProgram() {
        return program;
    }

    @Override
    public void enterTypedef(PrologParser.TypedefContext ctx) {
        List<String> typeNames = ctx.NAME().stream().map(ParseTree::getText).collect(Collectors.toList());
        Type type = parseType(ctx.type());
        for (String name : typeNames){
            program.domains().addType(name, type);
        }
    }

    private Type parseType(PrologParser.TypeContext ctx){
        if(ctx.primitiveType!=null)
            return program.domains().get(ctx.primitiveType.getText());
        if(ctx.listOf!=null)
            return new Type(ctx.listOf.getText());
        if(ctx.functorType()!=null)
            return parseFunctorType(ctx.functorType());
        throw new ParseException(ctx, "Can not read type description.");
    }

    private Type parseFunctorType(PrologParser.FunctorTypeContext ctx){
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
        return new FunctorType(
                name,
                ctx.argTypes().typeName()
                        .stream().map(RuleContext::getText)
                        .collect(Collectors.toList()));
    }

    @Override
    public void enterDatabase(PrologParser.DatabaseContext ctx) {
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
        program.database().addPredicate(predicate, dbName);
        program.domains().addDatabasePredicate(predicate);
    }

    @Override
    public void enterPredicates(PrologParser.PredicatesContext ctx) {
        ctx.predDef().forEach(this::parsePredicate);
    }

    private void parsePredicate(PrologParser.PredDefContext ctx){
        RuleExecutorPredicate predicate = new RuleExecutorPredicate(
                ctx.NAME().getText(),
                args(ctx.argTypes()),
                program.domains());
        program.predicates().add(predicate);
    }

    private List<String> args(PrologParser.ArgTypesContext ctx){
        return ctx.typeName()
                .stream().map(RuleContext::getText)
                .collect(Collectors.toList());
    }

    @Override
    public void enterClauses(PrologParser.ClausesContext ctx) {
        List<PrologParser.ClauseContext> clauses = ctx.clause();
        if(clauses ==null || clauses.isEmpty())
            return;
        clauses.forEach(this::parseClause);
    }

    private void parseClause(PrologParser.ClauseContext ctx){
        VariableStorage variables = new VariableStorage();
        String name = ctx.ruleLeft.NAME().getText();
        PrologParser.ArgListContext argList = ctx.ruleLeft.argList();
        int arity = 0;
        if(argList!=null && argList.value()!=null){
            arity = argList.value().size();
        }
    }

    /*private ValueModel parseValue(PrologParser.ValueContext ctx, VariableStorage variables, Type expected){
        if (ctx.VARNAME()!=null){
            VariableModel var = new VariableModel(ctx.VARNAME().getText());
            if(expected!=null) var.setType(expected);
            variables.put(var);
            return var;
        }
        if(ctx.integer()!=null){
            PrologParser.IntegerContext intCtx = ctx.integer();
            String str = intCtx.INTEGER().getText();
            int value;
            if(str.charAt(0)=='$'){
                str = str.substring(1);
                value = Integer.parseInt(str, 16);
            }else{
                value = Integer.parseInt(str);
            }
            if(intCtx.minus!=null){
                value = -value;
            }
            if(expected!=null){
                if(expected.equals(program.domains().get("real")))
                    return new SimpleValueModel(expected, (double)value);
                if(expected.equals(program.domains().get("char")))
                    return new SimpleValueModel(expected, (char)value);
            }
            return new SimpleValueModel(program.domains().get("integer"), value);
        }
        if(ctx.real()!=null){
            PrologParser.IntegerContext intCtx = ctx.integer();
            String str = intCtx.INTEGER().getText();
            double value = Double.parseDouble(str);
            if(intCtx.minus!=null){
                value = -value;
            }
            return new SimpleValueModel(program.domains().get("integer"), value);
        }
        if(ctx.STRING()!=null){
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

            if(expected!=null){
                if(expected.equals(program.domains().get("symbol")))
                    return new SimpleValueModel(expected, sb.toString());
                else return new SimpleValueModel(program.domains().get("string"), sb.toString());
            }
        }
        if(ctx.symbol!=null){
            return new SimpleValueModel(program.domains().get("symbol"), ctx.symbol.getText());
        }
        if(ctx.list()!=null){

        }
    }*/


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
