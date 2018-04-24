package ru.prolog.model.type.exceptions;

import ru.prolog.model.type.Type;

public class WrongFunctorNameException extends RuntimeException {
    String name;
    Type functorType;

    public WrongFunctorNameException(String name, Type functorType) {
        super("Functor functorName '"+name+"' does not exist in type " + functorType);
        this.name = name;
        this.functorType = functorType;
    }

    public WrongFunctorNameException(String message, String name, Type functorType) {
        super(message);
        this.name = name;
        this.functorType = functorType;
    }
}
