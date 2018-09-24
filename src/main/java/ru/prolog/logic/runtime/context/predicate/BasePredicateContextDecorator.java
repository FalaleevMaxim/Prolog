package ru.prolog.logic.runtime.context.predicate;

import ru.prolog.logic.model.managers.rule.RuleContextManager;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.rule.Statement;
import ru.prolog.logic.runtime.context.program.ProgramContext;
import ru.prolog.logic.runtime.context.rule.RuleContext;
import ru.prolog.logic.runtime.values.Value;

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
    public RuleContext ruleContext() {
        return decorated.ruleContext();
    }

    @Override
    public Statement statement() {
        return decorated.statement();
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

    @Override
    public String toString() {
        return decorated.toString();
    }
}
