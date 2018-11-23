package ru.prolog.runtime.context.rule;

import ru.prolog.model.ModelObject;
import ru.prolog.model.rule.Rule;
import ru.prolog.runtime.RuntimeObject;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.statements.ExecutedStatements;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

import java.util.Collection;
import java.util.List;

/**
 * Контекст вызова правила.
 * <p>
 * Содержит состояние, которое может изменяться при выполнении правила.
 * Содержит переменные, относящиеся к контексту вызова.
 */
public interface RuleContext extends RuntimeObject {
    /**
     * Возвращает ссылку на правило, которое вызывается.
     *
     * @return Ссылка на правило, которое было вызкано.
     */
    Rule rule();

    /**
     * Выполняет правило.
     *
     * @return Результат выполнения правила.
     */
    boolean execute();

    @Override
    default ModelObject model() {
        return rule();
    }

    /**
     * Возвращает аргументы, которые требуется передать правилу при вызове.
     *
     * @return Аргументы, которые требуется передать правилу при вызове.
     */
    List<Value> getArgs();

    /**
     * Возвращает зарегистрированную в контексте переменную по её имени.
     *
     * @param name Имя переменной.
     * @return Зарегистрированная в контексте переменная или {@code null} если переменной с таким именем в контексте нет.
     * @see #addVariable(Variable)
     */
    Variable getVariable(String name);

    /**
     * Регистрирует переменную в контексте.
     * <p>
     * Переменная содержит имя ({@link Variable#getName()}), по которому её можно будет получить с помощью {@link #getVariable(String)}.
     * <p>
     * Анонимная переменная (с именем {@code "_"}) не регистрируется в контексте!
     *
     * @param variable Переменная, которую нужно зарегистрировать в контексте.
     */
    void addVariable(Variable variable);

    /**
     * Возвращает объект, хранящий информацию о выполненных выражениях:
     * номер выполняемого выражения, список выполненных выражений, индекс последнего отсечения.
     *
     * @return Объект, хранящий информацию о выполненных выражениях
     */
    ExecutedStatements getStatements();

    /**
     * Возвращает коллекцию всех переменных, зарегистрированных в контексте.
     *
     * @return Все переменные, зарегистрированные в контексте. Если в контексте нет переменных, возвращает пустой список.
     */
    Collection<Variable> getVariables();

    /**
     * Возвращает ссылку на контекст программы, в рамках которого выполныется правило.
     *
     * @return Ссылка на контекст программы, в рамках которого выполныется правило.
     */
    ProgramContext programContext();

    /**
     * Возвращает контекст предиката, из которого было вызвано правило.
     *
     * @return Контекст предиката, из которого было вызвано правило.
     */
    PredicateContext getPredicateContext();

    /**
     * Откатывает переданные правилу аргументы к состоянию до вызова правила.
     */
    void rollback();

    /**
     * Используется вместо {@link #execute()}, когда требуется найти следующее решение.
     * Начинает выполнение с повторного выполнения последнего выполненного выражения, как будто после него был fail.
     * Если в правиле нет выражений, возвращает {@code false}.
     *
     * @return Результат поиска нового решения в правиле.
     */
    boolean redo();
}
