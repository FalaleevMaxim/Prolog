package ru.prolog.logic.runtime.context.rule;

import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.context.program.ProgramContext;
import ru.prolog.logic.runtime.context.rule.statements.ExecutedStatements;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;

import java.util.Collection;
import java.util.List;

public abstract class BaseRuleContextDecorator implements RuleContext {
    protected RuleContext decorated;

    protected BaseRuleContextDecorator(RuleContext decorated) {
        this.decorated = decorated;
    }

    @Override
    public Rule rule() {
        return decorated.rule();
    }

    @Override
    public List<Value> getArgs() {
        return decorated.getArgs();
    }

    @Override
    public Variable getVariable(String name) {
        return decorated.getVariable(name);
    }

    @Override
    public void addVariable(Variable variable) {
        decorated.addVariable(variable);
    }

    @Override
    public Collection<Variable> getVariables() {
        return decorated.getVariables();
    }

    @Override
    public ExecutedStatements getStatements() {
        return decorated.getStatements();
    }

    @Override
    public ProgramContext programContext() {
        return decorated.programContext();
    }

    @Override
    public PredicateContext getPredicateContext() {
        return decorated.getPredicateContext();
    }

    @Override
    public void rollback(){
        decorated.rollback();
    }

    @Override
    public String toString() {
        return decorated.toString();
    }
}
