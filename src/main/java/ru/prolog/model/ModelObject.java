package ru.prolog.model;

import ru.prolog.model.exceptions.ModelStateException;

import java.util.Collection;

public interface ModelObject {
    /**
     * Model object checks if its state and all its parts or dependent objects are ready for fixing.
     * If not, returns all exceptions.
     * @return all exceptions in state of sender and its dependent objects. If return is empty, object is ready for {@link #fix()}
     */
    Collection<ModelStateException> exceptions();

    /**
     * Disables all setters and other methods which change model object and fixes all dependent objects. Makes model unmodifiable.
     * It is recommended to call {@link #exceptions()} before calling this. If its return is not empty, any of the returned exceptions can be thrown.
     */
    ModelObject fix();
}