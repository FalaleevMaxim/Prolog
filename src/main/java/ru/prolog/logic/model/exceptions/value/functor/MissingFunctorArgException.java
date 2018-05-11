package ru.prolog.logic.model.exceptions.value.functor;

import ru.prolog.logic.model.type.descriptions.Functor;
import ru.prolog.logic.values.model.FunctorValueModel;

public class MissingFunctorArgException extends FunctorValueException {
    private final Functor functor;
    private final int argNum;

    public MissingFunctorArgException(FunctorValueModel functorValue, Functor functor, int argNum) {
        this(functorValue, functor, argNum,
                String.format("Functor %s requires %dth argument of type %s",
                        functor, argNum, functor.getArgTypeNames().get(argNum)));
    }

    public MissingFunctorArgException(FunctorValueModel functorValue, Functor functor, int argNum, String message) {
        super(functorValue, message);
        this.functor = functor;
        this.argNum = argNum;
    }

    public Functor getFunctor() {
        return functor;
    }

    public int getArgNum() {
        return argNum;
    }
}
