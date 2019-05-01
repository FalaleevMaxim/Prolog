package ru.prolog.model;

import ru.prolog.compiler.position.ModelCodeIntervals;
import ru.prolog.etc.exceptions.model.ModelStateException;

import java.util.Collection;

/**
 * Основные поля и методы для упрощения реализации {@link ModelObject}.
 * Рекомендуется не реализовывать {@link ModelObject} напрямую, а унаследовать объект модели от этого класса.
 */
public abstract class AbstractModelObject implements ModelObject {
    /**
     * Интервал исходного кода для реализации {@link #getCodeIntervals()} и {@link #setCodeIntervals(ModelCodeIntervals)}
     */
    protected ModelCodeIntervals intervals;

    /**
     * Флаг зафиксированного состояния объекта. Изначально {@code false}, после успешного {@link #fix()} устанавливается в {@code true}.
     * Рекомендуется привязывать к этому флагу всю логику, связанную с блокировкой методов в зависимости от фиксирования состояния.
     * Для сеттеров и прочих модифицирующих состояние методов в начале писать:
     * <pre>
     *     if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
     * </pre>
     * Для методов, которые должны использоваться при выполнении программы, в начале писать:
     * <pre>
     *     if(!fixed) throw new IllegalStateException("Object is not fixed. You can not yse it yet.");
     * </pre>
     */
    protected boolean fixed = false;

    @Override
    public ModelCodeIntervals getCodeIntervals() {
        return intervals;
    }

    @Override
    public void setCodeIntervals(ModelCodeIntervals pos) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.intervals = pos;
    }

    /**
     * Основной шаблон реализации метода {@link ModelObject#fix()}.
     * Вызывает {@link #exceptions()}, и если список не пустой, бросает первое из исключений.
     * Если ошибок нет, проставляет флаг {@link #fixed} в {@code true} и вызывает логику фиксирования объекта, переопределяемую в {@link #fixIfOk()}.
     *
     * @return {@code this}.
     */
    @Override
    public ModelObject fix() {
        if(fixed) return this;
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty())
            throw exceptions.iterator().next();
        fixed = true;
        fixIfOk();
        return this;
    }

    /**
     * Переопределяемая логика, что делать при фиксировании объекта если нет ошибок.
     * Здесь следует вызывать {@link #fix()} у зависимых объектов и заменять в полях изменяемые объекты на неизменяемые.
     */
    protected void fixIfOk(){}
}
