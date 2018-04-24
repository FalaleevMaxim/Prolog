package ru.prolog.context.rule;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.model.type.Type;
import ru.prolog.model.rule.Rule;
import ru.prolog.values.Value;
import ru.prolog.values.variables.Variable;

import java.util.Collection;
import java.util.List;

public abstract class BaseRuleContextDecorator implements RuleContext {
    protected RuleContext decorated;

    public BaseRuleContextDecorator(RuleContext decorated) {
        this.decorated = decorated;
    }

    @Override
    public Rule getRule() {
        return decorated.getRule();
    }

    @Override
    public List<Value> getArgs() {
        return decorated.getArgs();
    }

    @Override
    public Variable getVariable(String name, Type type) {
        return decorated.getVariable(name, type);
    }

    @Override
    public Collection<Variable> getVariables() {
        return decorated.getVariables();
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
    public boolean redo() {
        return decorated.redo();
    }

    @Override
    public void rollback(){
        decorated.rollback();
    }
}
