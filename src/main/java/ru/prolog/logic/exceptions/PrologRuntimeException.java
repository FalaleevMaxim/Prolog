package ru.prolog.logic.exceptions;

public class PrologRuntimeException extends RuntimeException {
    public PrologRuntimeException(String message) {
        super(message);
    }

    public PrologRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
