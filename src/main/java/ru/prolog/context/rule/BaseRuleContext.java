package ru.prolog.context.rule;


import ru.prolog.WrongTypeException;
import ru.prolog.model.Type;
import ru.prolog.model.backup.Backup;
import ru.prolog.model.backup.RelatedBackup;
import ru.prolog.model.backup.ValueBackup;
import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.values.Value;
import ru.prolog.values.variables.Variable;

import java.util.*;
import java.util.stream.Collectors;

public class BaseRuleContext implements RuleContext {
    private Rule rule;
    private List<Value> args;
    private Map<String, Variable> variables;
    private PredicateContext context;
    private List<Backup> backups;
    private ExecutedStatements statements;

    public BaseRuleContext(Rule rule, List<Value> args) {
        this.rule = rule;
        this.args = args;
    }

    public BaseRuleContext(Rule rule, List<Value> args, PredicateContext context) {
        this(rule, args);
        this.context = context;
    }

    public Rule getRule() {
        return rule;
    }

    public List<Value> getArgs() {
        return args;
    }

    @Override
    public Variable getVariable(String name, Type type) {
        Variable variable;
        if(variables == null) variables = new HashMap<>();
        if(variables.containsKey(name)){
            variable = variables.get(name);
            if(variable.getType() != type)
                throw new WrongTypeException(
                        "Type of requested variable does not match type of stored variable",
                        type, variable.getType());
            return variable;
        }else{
            variable = type.createVariable(name);
            variables.put(name, variable);
            return variable;
        }
    }

    @Override
    public Collection<Variable> getVariables() {
        if(variables == null) return Collections.emptyList();
        return variables.values();
    }

    @Override
    public PredicateContext getPredicateContext() {
        return context;
    }

    @Override
    public boolean execute() {
        makeBackups();
        if(rule.run(args, this)){
            return true;
        }
        else{
            rollback();
            return false;
        }
    }

    private void makeBackups() {
        backups = getArgs().stream()
                .filter(value -> value instanceof Variable)
                .map(value -> (Variable)value)
                .map(ValueBackup::new)
                .map(RelatedBackup::new)
                .collect(Collectors.toList());
    }

    public void rollback(){
        backups.forEach(Backup::rollback);
    }

    @Override
    public boolean redo() {
        if(statements == null || statements.executions.isEmpty()){
            return false;
        }
        if(rule.run(args, this)){
            return true;
        }
        else{
            rollback();
            return false;
        }
    }

    @Override
    public ExecutedStatements getStatements() {
        if(statements ==null) statements = new ExecutedStatements();
        return statements;
    }
}
