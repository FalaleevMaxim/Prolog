package ru.prolog.runtime.context.predicate;

import ru.prolog.model.ModelObject;
import ru.prolog.model.managers.rule.RuleContextManager;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.Statement;
import ru.prolog.runtime.RuntimeObject;
import ru.prolog.runtime.context.ExecutionContext;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;

import java.util.List;

public interface PredicateContext extends ExecutionContext, RuntimeObject {
    ProgramContext programContext();

    RuleContext ruleContext();

    Statement statement();

    Predicate predicate();

    @Override
    default ModelObject model() {
        return statement()!=null?statement():predicate();
    }

    List<Value> getArgs();
    RuleContextManager getRuleManager();

    /**
     * Put sender to context storage. Only this predicate in this execution can access data.
     * Data will be stored between backtracks to execution and deleted after predicate returned fail.
     */
    void putContextData(String key, Object data);

    /**
     * Get sender from context storage.
     * @param key key by which sender was put with {@link #putContextData(String, Object) putContextData} method
     * @return sender by key or null
     */
    Object getContextData(String key);

    /**
     * Makes predicate not to try next rules after inner getRule met cut predicate.
     */
    void cut();

    /**
     * @return true after {@link #cut() cut()} method was executed, otherwise false.
     */
    boolean isCut();

    /**
     * Возвращает {@code true}, если предикат вернул {@link ru.prolog.model.predicate.PredicateResult#FAIL fail}, и контекст больше нельзя использовать
     */
    boolean failed();
}
