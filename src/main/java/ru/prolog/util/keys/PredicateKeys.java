package ru.prolog.util.keys;

/**
 * Константы ключей в контексте предиката
 *
 * @see ru.prolog.runtime.context.predicate.PredicateContext#getContextData(String)
 * @see ru.prolog.runtime.context.predicate.PredicateContext#putContextData(String, Object)
 */
public class PredicateKeys {
    private PredicateKeys(){}

    /**
     * Ключ, по которому в контексте предиката предикаты могут хранить номер последнего выполненного правила
     */
    public static final String START_WITH_RULE = "predicate.startWith";

    /**
     * Используется предикатом {@link ru.prolog.std.db.RetractPredicate retract} для сохранения количеста правил после удаления.
     */
    public static final String RETRACT_RULE_COUNT = "retract.rule_count";
}
