package ru.prolog.runtime.context.rule.statements;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранит состояние вызовов внутри правила.
 */
public class ExecutedStatements {
    /**
     * Список отработавших предикатов.
     * Может содержать null на месте предикатов, которые вернули {@link ru.prolog.model.predicate.PredicateResult#LAST_RESULT},
     * их нужно пропустить при бэктрекинге
     * Выражения, не являющиеся null, нужно запускать заново при бэктрекинге.
     */
    public List<ExecutedStatement> executions = new ArrayList<>();

    /**
     * Индекс последнего выражения-отсечения.
     * Когда при выполнении правила встречается отсечение, его индекс {@link #currentStatement} становится индексом отсечения,
     * а все выражения до отсечения (включительно) удаляются из списка {@link #executions}, т.к. бэктрекинг к ним становится невозможен.
     */
    public int cutIndex = -1;

    /**
     * Номер текущего выражения, выполняемого правилом.
     * Соответствует номеру выражения в теле правила, но не обязательно соответствует номеру выражения в
     */
    public int currentStatement = 0;

    /**
     * Номер текущего списка выражений в теле правила.
     * Списки - это дизъюнкты, разделяемые ";" в коде правила.
     */
    public int currentList = 0;
}
