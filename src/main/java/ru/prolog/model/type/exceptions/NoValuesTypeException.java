package ru.prolog.model.type.exceptions;

import ru.prolog.model.type.Type;

public class NoValuesTypeException extends RuntimeException{
    public final Type type;

    public NoValuesTypeException(Type type) {
        this("Type does not support creating values or variables.", type);
    }

    public NoValuesTypeException(String message, Type type) {
        super(message);
        this.type = type;
    }
}
