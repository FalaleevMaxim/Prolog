package ru.prolog.logic.model.type.exceptions;

import ru.prolog.logic.model.type.Type;

public class WrongTypeException extends RuntimeException {
    private Type expected;
    private Type got;

    public WrongTypeException(String message, Type expected, Type got) {
        super(message);
        this.expected = expected;
        this.got = got;
    }

    public Type expectedType() {
        return expected;
    }

    public Type gotType() {
        return got;
    }
}
