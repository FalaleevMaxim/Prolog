package ru.prolog.logic.model;

public interface ModelBuilder<T extends ModelObject> {
    T create();
}
