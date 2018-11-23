package ru.prolog.runtime.context.predicate;

import ru.prolog.model.managers.rule.RuleContextManager;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.Statement;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;

import java.util.List;

/**
 * Абстрактный декоратор для контекста предиката.
 * Реализует все методы, кроме {@link PredicateContext#execute()}, делегируя их к декорируемому объекту.
 */
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
    public boolean failed() {
        return decorated.failed();
    }

    @Override
    public String toString() {
        return decorated.toString();
    }
}
