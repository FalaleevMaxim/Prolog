package ru.prolog.logic.model.type.exceptions;

import ru.prolog.logic.exceptions.PrologRuntimeException;
import ru.prolog.logic.model.type.Type;

public class NoValuesTypeException extends PrologRuntimeException {
    public final Type type;

    public NoValuesTypeException(Type type) {
        this("Type does not support creating values or variables.", type);
    }

    public NoValuesTypeException(String message, Type type) {
        super(message);
        this.type = type;
    }
}
