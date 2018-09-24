package ru.prolog.logic.etc.exceptions.model.value.functor;

import ru.prolog.logic.etc.exceptions.model.value.ValueStateException;
import ru.prolog.logic.model.type.descriptions.Functor;
import ru.prolog.logic.model.values.FunctorValueModel;
import ru.prolog.util.ToStringUtil;

public class MissingFunctorArgException extends ValueStateException {
    private final Functor functor;
    private final int argNum;

    public MissingFunctorArgException(FunctorValueModel functorValue, Functor functor, int argNum) {
        this(functorValue, functor, argNum,
                String.format("Functor %s requires %s argument of type %s",
                        functor, ToStringUtil.ordinal(argNum), functor.getArgTypeNames().get(argNum)));
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
