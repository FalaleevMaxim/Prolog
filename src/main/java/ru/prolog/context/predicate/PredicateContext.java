package ru.prolog.context.predicate;

import ru.prolog.context.Executable;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.values.Value;

import java.util.List;

public interface PredicateContext extends Executable {
    ProgramContext programContext();
    RuleContext getRuleContext();
    Predicate getPredicate();
    List<Value> getArgs();

    /**
     * @return last rule context which successfully executed in predicate. Null if predicate never been executed before or last rule failed.
     */
    RuleContext getLastExecutedRuleContext();
    void setLastExecutedRuleContext(RuleContext lastExecutedRuleContext);

    /**
     * Put object to context storage. Only this predicate in this execution can access data.
     * Data will be stored between backtracks to execution and deleted after predicate returned fail.
     */
    void putContextData(String key, Object data);

    /**
     * Get object from context storage.
     * @param key key by which object was put with {@link #putContextData(String, Object) putContextData} method
     * @return object by key or null
     */
    Object getContextData(String key);

    /**
     * Makes predicate not to try next rules after inner rule met cut predicate.
     */
    void cut();

    /**
     * @return true after {@link #cut() cut()} method was executed, otherwise false.
     */
    boolean isCut();
}
