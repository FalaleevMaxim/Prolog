package ru.prolog.context.rule;


import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.context.rule.statements.ExecutedStatements;
import ru.prolog.model.type.exceptions.WrongTypeException;
import ru.prolog.model.type.Type;
import ru.prolog.model.rule.Rule;
import ru.prolog.values.Value;
import ru.prolog.values.variables.Variable;
import ru.prolog.values.variables.backup.Backup;
import ru.prolog.values.variables.backup.RelatedBackup;
import ru.prolog.values.variables.backup.ValueBackup;

import java.util.*;
import java.util.stream.Collectors;

public class BaseRuleContext implements RuleContext {
    private Rule rule;
    private List<Value> args;
    private Map<String, Variable> variables;
    private ProgramContext programContext;
    private PredicateContext context;
    private List<Backup> backups;
    private ExecutedStatements statements;

    public BaseRuleContext(Rule rule, List<Value> args, ProgramContext programContext) {
        this.programContext = programContext;
        this.rule = rule;
        this.args = args;
    }

    public BaseRuleContext(Rule rule, List<Value> args, PredicateContext context) {
        this(rule, args, context.programContext());
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
        }
        return null;
    }

    @Override
    public void addVariable(Variable variable) {
        variables.put(variable.getName(), variable);
    }

    @Override
    public Collection<Variable> getVariables() {
        if(variables == null) return Collections.emptyList();
        return variables.values();
    }

    @Override
    public ProgramContext programContext() {
        return programContext;
    }

    @Override
    public PredicateContext getPredicateContext() {
        return context;
    }

    @Override
    public boolean execute() {
        argBackups();
        if(rule.run(args, this)){
            return true;
        }
        else{
            rollback();
            return false;
        }
    }

    private void argBackups() {
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
