package ru.prolog.logic.etc.exceptions.runtime;

public class PrologRuntimeException extends RuntimeException {
    public PrologRuntimeException(String message) {
        super(message);
    }

    public PrologRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
