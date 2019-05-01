package ru.prolog.model.type.descriptions;

import ru.prolog.model.ModelObject;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.model.type.Type;

import java.util.List;

/**
 * Интерфейс функтора, который может входить в состав составного типа.
 */
public interface Functor extends ModelObject {
    /**
     * Возвращает составной тип, которому принадлежит функтор
     */
    CompoundType getCompoundType();

    /**
     * Возвращает имя функтора
     */
    String getName();

    /**
     * Возвращает имена типов аргументов функтора. Список может быть пустым.
     */
    List<String> getArgTypeNames();

    /**
     * Возвращает ссылку на хранилище типов, в котором нужно искать типы аргументов (может быть null если функтор не имеет аргументов).
     */
    TypeStorage getTypeStorage();

    /**
     * Устанавливает составной тип, которому будет принадлежать функтор.
     *
     * @param type Составной тип.
     */
    void setCompoundType(CompoundType type);

    /**
     * Устанавливает хранилище типов, которое будет использоваться для поиска типов аргументов.
     * @param typeStorage Хранилище типов
     */
    void setTypeStorage(TypeStorage typeStorage);

    /**
     * Возвращает список типов аргументов, используя {@link #getArgTypeNames()} и {@link #getTypeStorage()}
     */
    List<Type> getArgTypes();
}