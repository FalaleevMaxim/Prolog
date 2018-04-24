package ru.prolog.model;

public interface ModelBuilder<T extends ModelObject> {
    T create();
}
