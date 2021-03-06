package ru.prolog.model;

import ru.prolog.compiler.position.ModelCodeIntervals;
import ru.prolog.etc.exceptions.model.ModelStateException;

import java.util.Collection;

/**
 * Объект модели программы. Описывает структуру программы.
 * Может находиться в двух состояниях.
 * Изначально после создания может изменяться, все сеттеры и прочие методы изменения объекта доступны, поля-коллекции чаще всего редактируемы.
 * Но на этой стадии не может использоваться по назначению и при попытке использования бросает {@link IllegalStateException}.
 * Затем следует вызвать метод {@link #exceptions()} чтобф проверить целостность объекта, всех его составных частей и связей, и получить список ошибок.
 * Если список ошибок пустой, следует вызвать метод {@link #fix()} который зафиксирует состояние объекта, всех его частей и зависимых объектов.
 * В начале вызова {@link #fix()} будет вызван {@link #exceptions()}, и если список ошибок не пустой, будет брошено первое из исключений.
 * После успешного выполнения {@link #fix()} объект уже нельзя редактировать: все сеттеры и прочие изменяющие состояние методы бросают {@link IllegalStateException}, а все поля-коллекции заменяются да неизменяемые.
 * После этого объект становится возможно использовать по назначению в программе.
 */
public interface ModelObject {
    /**
     * Проверяет, готово ли к фиксированию состояние объекта, всех его частей и зависимых объектов.
     * Если нет, возвращает все найденные ошибки.
     * @return Все ошибки в состоянии этого объекта, его частей и зависимых объектов. Если возвращает пустой список, объект готов для фиксирования ({@link #fix()})
     */
    Collection<ModelStateException> exceptions();

    /**
     * Фиксирует состояние объекта, всех его частей и зависимых объектов.
     * Блокирует сеттеры и прочие изменяющие состояние объекта методы, меняет поля-коллекции на неизменяемые. Таким образом, вся модель программы становится неизменяемой.
     * азблокирует методы, которые используются при выполнении программы.
     * Рекомендуется вызывать {@link #exceptions()} перед вызовом этого метода. Если список ошибок не пустой, будет выброшена одна из них.
     */
    ModelObject fix();

    /**
     * Возвращает интервал исходного кода, который объявляет данных объект.
     */
    ModelCodeIntervals getCodeIntervals();

    /**
     * Устанавливает интервал исходного кода, который объявляет данных объект.
     *
     * @param pos Интервал кода.
     */
    void setCodeIntervals(ModelCodeIntervals pos);
}