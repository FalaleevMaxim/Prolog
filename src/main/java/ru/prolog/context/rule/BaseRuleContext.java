package ru.prolog.context.rule;


import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.context.rule.statements.ExecutedStatements;
import ru.prolog.model.rule.Rule;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;
import ru.prolog.backup.Backup;

import java.util.*;

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
    public Variable getVariable(String name) {
        if(variables==null) return null;
        return variables.get(name);
    }

    @Override
    public void addVariable(Variable variable) {
        if(variables == null) variables = new HashMap<>();
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
        backups = new LinkedList<>();
        for(Value arg : args){
            for(Variable var : arg.innerFreeVariables()){
                backups.add(programContext.program().managers().getBackupManager().backup(var));
            }
        }
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
