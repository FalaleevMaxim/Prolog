package ru.prolog.model.type.exceptions;

import ru.prolog.model.type.Type;

import java.util.List;

public class WrongFunctorTypesException extends RuntimeException{
    public final Type functorType;
    public final List<String> expected;
    public final List<String> real;

    public WrongFunctorTypesException(Type functorType, List<String> expected, List<String> real) {
        super("Wrong types of functor arguments. Expected: " + expected + " Real: " + real);
        this.functorType = functorType;
        this.expected = expected;
        this.real = real;
    }

    public WrongFunctorTypesException(String message, Type functorType, List<String> expected, List<String> real) {
        super(message);
        this.functorType = functorType;
        this.expected = expected;
        this.real = real;
    }
}
