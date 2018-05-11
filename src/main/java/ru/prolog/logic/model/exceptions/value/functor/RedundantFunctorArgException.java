package ru.prolog.logic.model.exceptions.value.functor;

import ru.prolog.logic.values.model.FunctorValueModel;
import ru.prolog.logic.values.model.ValueModel;

public class RedundantFunctorArgException extends FunctorValueException {
    private final int argNum;

    public RedundantFunctorArgException(FunctorValueModel functorValue, int argNum) {
        this(functorValue, argNum,
                String.format("%dth argument %s is redundant. Functor %s requires %d arguments.",
                        argNum,
                        functorValue.getArgs().get(argNum),
                        functorValue.getType().getCompoundType().getFunctor(functorValue.getFunctorName()),
                        functorValue.getType().getCompoundType().getFunctor(functorValue.getFunctorName()).getArgTypeNames().size()));
    }

    public RedundantFunctorArgException(FunctorValueModel sender, int argNum, String message) {
        super(sender, message);
        this.argNum = argNum;
    }

    public ValueModel getArg() {
        return getFunctorValue().getArgs().get(argNum);
    }

    public int getArgNum() {
        return argNum;
    }
}
