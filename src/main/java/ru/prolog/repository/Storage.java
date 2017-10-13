package ru.prolog.repository;

public interface Storage<T> {
    T get(String name);
}
