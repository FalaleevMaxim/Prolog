package ru.prolog.logic.managers.option;

public interface Option<T> {
    /**
     * Uses "decorator" pattern to add behaviot to objects.
     * @param decorated Object to add behavior
     * @return decorator containing decorated sender.
     */
    T decorate(T decorated);
}