package ru.prolog.runtime.values;

import ru.prolog.model.type.Type;
import ru.prolog.model.values.ValueModel;
import ru.prolog.runtime.context.predicate.PredicateContext;

import java.util.List;

/**
 * Базовый интерфейс для всех значений в программе (переменные, примитивы, списки, функторы).
 * Основная функция инерфейса - унификация ({@link #unify(Value)})
 * <p>
 * Этот интерфейс представляет значения во время выполнения программы.
 * В аргументах правил и выражений в модели программы используется {@link ValueModel}.
 */
public interface Value {
    /**
     * Возвращает содержимое значения.
     * <p>
     * Для значения типа integer возвращает {@link Integer}.
     * Для значений типа string или symbol возвращает {@link String}
     * Для значений типа char возвращает {@link Character}
     * Для значений типа real возвращает {@link Double}
     * Для списков возвращает голову списка ({@link Value}), либо {@code null} если список пустой.
     * Для функторов возвращает список внутренних аргументов функтора ({@code List<Value>}).
     * Для свободной переменной любого типа возвращает {@code null}.
     *
     * @return Содержимое значения. Для свободных переменных {@code null}.
     */
    Object getContent();

    /**
     * Возвращает объект, описывающий тип значения.
     *
     * @return Тип значения. Не может быть {@code null}.
     */
    Type getType();

    /**
     * Унифицирует значение с переданным значением.
     * <p>
     * Логика унификации определяется типом значения. В обще виде, при унификации двух значений:
     * <ul>
     * <li>Если оба значения не являются свободными переменными и имеют примитивный тип, возвращается результат сравнения содержимого.</li>
     * <li>Если оба значения не являются свободными переменными и имеют сложный тип (список, функтор), их содержимое реурсивно унифицируется.
     * Если хотя бы одна унификация вернула {@code false}, метод возвращает {@code false}</li>
     * <li>Нсли одно из значений является свободной переменной, а другое не является свободной переменной, свободная переменная получает содержимое другого значения
     * (и передаёт его связанным переменным). Унификация при этом возвращает {@code true}.</li>
     * <li>Если оба значения являются свободными переменными, переменные связываются, и когда одна из связанных переменных получит содержимое, она передаст его всем связанным.</li>
     * </ul>
     * <p>
     * Операция унификации является симметричной: результат {@code a.unify(b) } должен быть равен {@code b.unify(a) }.
     * В некоторых случаях внутри метода можно делегировать вызов к обратному (вызвать {@param other}.unify({@code this})), не вызывая при этом бесконечной рекурсии.
     *
     * @param other Значение, с которым унифицируется данное значение.
     * @return Результат унификации. {@code true}, если унификация прошла успешно, {@code false} если есть хотя бы 1 несоответствие между значениями.
     */
    boolean unify(Value other);

    /**
     * Возвращает все свободные переменные внутри значения. Чаще всего вызывается рекурсивно.
     * <p>
     * Свободные переменные возвращают коллекцию из одного элемента - себя.
     * Константы и несвободные переменные примитивных типов возвращают пустую коллекцию.
     * Все остальные значения вызывают метод рекурсивно на своём содержимом и объединяют полученные коллекции.
     *
     * @return Коллекция всех свободных переменных внутри значения.
     */
    List<Variable> innerFreeVariables();

    /**
     * Приводит значение времени выполнения к модели значения.
     * <b>НE возвращает объект в модели, на основе котороко создан объект!</b>
     * <p>
     * Метод предназначен в первую очередь для предикатаов assert. В них рантайм-структура переводится в модель правила-факта базы данных.
     * Этот метод приводит объект к модели в соответствии с содержимым на момент вызова. Таким образом, переменная, не являющаяся свободной,
     * возвращает не модель переменной, а соответствующую содержимому модель значения.
     *
     * @return Модель значения, построенная в соответствии с содержимым значения.
     * @see ru.prolog.std.db.AbstractAssertPredicate#run(PredicateContext, List)
     */
    ValueModel toModel();
}
