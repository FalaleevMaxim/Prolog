package ru.prolog.logic.model.exceptions.value.functor;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.type.descriptions.Functor;
import ru.prolog.logic.values.model.FunctorValueModel;
import ru.prolog.logic.values.model.ValueModel;

public class WrongFunctorSubObjectTypeException extends FunctorValueException {
    private final Functor functor;
    private final int argNum;

    public WrongFunctorSubObjectTypeException(Functor functor, FunctorValueModel functorValue, int argNum) {
        this(functor, functorValue, argNum,
                "Wrong subObject type. " +
                        functorValue.getArgs().get(argNum) + " is not of type " + functor.getArgTypes().get(argNum));
    }

    public WrongFunctorSubObjectTypeException(Functor functor, FunctorValueModel functorValue, int argNum, String message) {
        super(functorValue, message);
        this.functor = functor;
        this.argNum = argNum;
    }

    public Functor getFunctor() {
        return functor;
    }

    public ValueModel getArg() {
        return getFunctorValue().getArgs().get(argNum);
    }

    public Type getExpectedType() {
        return functor.getArgTypes().get(argNum);
    }

    public String getExpectedTypeName() {
        return functor.getArgTypeNames().get(argNum);
    }
}
