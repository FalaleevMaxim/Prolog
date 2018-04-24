package ru.prolog.context.predicate;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.values.Value;

import java.util.List;

public abstract class BasePredicateContextDecorator implements PredicateContext {
    private PredicateContext decorated;

    public BasePredicateContextDecorator(PredicateContext decorated) {
        this.decorated = decorated;
    }

    @Override
    public Predicate getPredicate() {
        return decorated.getPredicate();
    }

    @Override
    public List<Value> getArgs() {
        return decorated.getArgs();
    }

    @Override
    public ProgramContext programContext() {
        return decorated.programContext();
    }

    @Override
    public void putContextData(String key, Object data) {
        decorated.putContextData(key, data);
    }

    @Override
    public Object getContextData(String key) {
        return decorated.getContextData(key);
    }

    @Override
    public void cut() {
        decorated.cut();
    }

    @Override
    public boolean isCut() {
        return decorated.isCut();
    }

    @Override
    public RuleContext getLastExecutedRuleContext() {
        return decorated.getLastExecutedRuleContext();
    }
}
