package ru.prolog.logic.etc.exceptions.runtime;

import ru.prolog.logic.model.type.Type;

public class WrongTypeException extends PrologRuntimeException {
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
