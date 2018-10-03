package ru.prolog.util.io;

import ru.prolog.etc.exceptions.runtime.PrologRuntimeException;

public interface ErrorListener extends OutputDevice {
    void prologRuntimeException (PrologRuntimeException e);
    void runtimeException (RuntimeException e);
}
