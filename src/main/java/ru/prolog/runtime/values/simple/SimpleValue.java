package ru.prolog.runtime.values.simple;

import ru.prolog.model.type.Type;
import ru.prolog.model.values.SimpleValueModel;
import ru.prolog.model.values.ValueModel;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.runtime.values.expression.ExprValue;
import ru.prolog.util.ToStringUtil;

import java.util.Collections;
import java.util.List;

/**
 * Значение примитивного типа.
 * <p>
 * Объект является неизменяемым, т.к. изначально имеет содержимое, и при унификации не меняет,
 * только либо сравнивает, либо передаёт, своё содержимое.
 */
public class SimpleValue implements Value {
    /**
     * Описание типа данных.
     */
    protected Type type;

    /**
     * Содержимое.
     */
    protected Object content;

    /**
     * Создаёт объект примитивного значения с заданным типом и содержимым.
     *
     * @param type    Тип данных.
     * @param content Содержимое.
     */
    public SimpleValue(Type type, Object content) {
        if (content == null) throw new IllegalArgumentException("Simple value content is null.");
        this.type = type;
        this.content = content;
    }

    /**
     * Конструктор предназначен для вызова {@code super()} в конструкторе {@link SimpleVariable}.
     * Позволяет установить {@link #content} в {@code null}.
     *
     * @param type Тип данных.
     */
    protected SimpleValue(Type type) {
        this.type = type;
    }

    /**
     * Если переданное значение является свободной переменной, вызывается унификация в обратном направлении,
     * т.к. свободная переменная должна получить содержимое, а а прямого доступа на запись содержимого переменная не предоставляет.
     * Возвращает {@code true}.
     * <p>
     * Если переданное значение является математическим выражением, унификация вызывается в обратном направлении,
     * т.к. выражение содержит логику выполнения, в т.ч. выполнения в направлении подсчёта свободной переменной в выражении.
     * Возвращает {@code true}.
     * <p>
     * В оставшихся случаях сравнивается содержимое значений.
     *
     * @param other Значение, с которым унифицируется данное значение.
     * @return Результат унификации значений.
     */
    @Override
    public boolean unify(Value other) {
        if (other instanceof Variable && ((Variable) other).isFree()) {
            return other.unify(this);
        }
        if (other instanceof ExprValue && ((ExprValue) other).hasFreeVariables()) {
            return other.unify(this);
        }
        return content.equals(other.getContent());
    }

    @Override
    public Object getContent() {
        return content;
    }

    @Override
    public Type getType() {
        return type;
    }

    /**
     * Возвращает все свободные переменные внутри значения.
     * В константе примитивного типа не может быть переменных, поэтому возвращает пустой список.
     *
     * @return Пустой список.
     */
    @Override
    public List<Variable> innerFreeVariables() {
        return Collections.emptyList();
    }

    @Override
    public ValueModel toModel() {
        return new SimpleValueModel(type, content);
    }

    @Override
    public String toString() {
        return ToStringUtil.simpleToString(type, content);
    }
}
