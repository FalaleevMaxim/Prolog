package ru.prolog.logic.context.predicate;

import ru.prolog.logic.context.Executable;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.managers.rule.RuleContextManager;
import ru.prolog.logic.values.Value;

import java.util.List;

public interface PredicateContext extends Executable {
    ProgramContext programContext();
    RuleContext getRuleContext();
    Predicate predicate();
    List<Value> getArgs();
    RuleContextManager getRuleManager();

    /**
     * @return last getRule context which successfully executed in predicate. Null if predicate never been executed before or last getRule failed.
     */
    RuleContext getLastExecutedRuleContext();
    void setLastExecutedRuleContext(RuleContext lastExecutedRuleContext);

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
}
