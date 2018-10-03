package ru.prolog.model.predicate;

/**
 * Результат вызова предиката
 */
public enum PredicateResult {
    /**
     * Предикат не нашёл решения
     */
    FAIL,

    /**
     * Предикат нашёл последнее решение. Предикат больше не будет вызван при бэктрекинге
     */
    LAST_RESULT,

    /**
     * Предикат нашёл решение, возможно будут ещё решения. При бэктрекинге предикат будет вызван повторно.
     */
    NEXT_RESULT;

    /**
     * Приводит результат выполнения предиката к boolean.
     * Аналогичен {@link #success()}
     *
     * @return {@code true} если это не {@link #FAIL}; {@code false} если {@link #FAIL}.
     */
    public boolean toBoolean() {
        return success();
    }

    /**
     * Возврашает {@code true} если предикат выполнился успешно (не {@link #FAIL})
     *
     * @return {@code true} если это не {@link #FAIL}; {@code false} если {@link #FAIL}.
     */
    public boolean success() {
        return this != FAIL;
    }

    /**
     * Возвращает {@code true} при {@link #FAIL}.
     *
     * @return {@code true} если это {@link #FAIL}, {@code false} в остальных случаях.
     */
    public boolean fail() {
        return this == FAIL;
    }

    /**
     * Возвращает {@code true} если предикат должен быть вызван повторно при бэктрекинге
     *
     * @return {@code true} если это {@link #NEXT_RESULT}
     */
    public boolean canRunAgain() {
        return this == NEXT_RESULT;
    }
}