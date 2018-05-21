package ru.prolog.logic.context.predicate;

import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.managers.rule.RuleContextManager;
import ru.prolog.logic.values.Value;

import java.util.List;

public abstract class BasePredicateContextDecorator implements PredicateContext {
    protected final PredicateContext decorated;

    public BasePredicateContextDecorator(PredicateContext decorated) {
        this.decorated = decorated;
    }

    @Override
    public Predicate predicate() {
        return decorated.predicate();
    }

    @Override
    public RuleContext getRuleContext() {
        return decorated.getRuleContext();
    }

    @Override
    public List<Value> getArgs() {
        return decorated.getArgs();
    }

    @Override
    public RuleContextManager getRuleManager() {
        return decorated.getRuleManager();
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
    public void setLastExecutedRuleContext(RuleContext lastExecutedRuleContext) {
        decorated.setLastExecutedRuleContext(lastExecutedRuleContext);
    }

    @Override
    public RuleContext getLastExecutedRuleContext() {
        return decorated.getLastExecutedRuleContext();
    }
}
