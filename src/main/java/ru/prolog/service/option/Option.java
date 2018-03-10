package ru.prolog.service.option;

public interface Option<T> {
    /**
     * Uses "decorator" pattern to add behaviot to objects.
     * @param decorated Object to add behavior
     * @return decorator containing decorated object.
     */
    T decorate(T decorated);
}