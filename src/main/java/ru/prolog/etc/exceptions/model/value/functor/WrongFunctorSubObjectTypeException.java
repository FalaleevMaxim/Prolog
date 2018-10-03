package ru.prolog.etc.exceptions.model.value.functor;

import ru.prolog.etc.exceptions.model.value.ValueStateException;
import ru.prolog.model.type.descriptions.Functor;
import ru.prolog.model.values.FunctorValueModel;

public class WrongFunctorSubObjectTypeException extends ValueStateException {
    public WrongFunctorSubObjectTypeException(Functor functor, FunctorValueModel functorValue, int argNum) {
        this(functorValue, argNum,
                "Wrong subObject type. " +
                        functorValue.getArgs().get(argNum) + " is not of type " + functor.getArgTypes().get(argNum));
    }

    public WrongFunctorSubObjectTypeException(FunctorValueModel functorValue, int argNum, String message) {
        super(functorValue.getArgs().get(argNum), message);
    }
}
