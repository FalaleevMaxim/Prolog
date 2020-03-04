package ru.prolog.util.io;

public interface OutputDevice {
    void print(String s);
    void println(String s);
    default void clear() {}
}
