package ru.prolog.storage;

public interface Storage<T> {
    T get(String name);
}
