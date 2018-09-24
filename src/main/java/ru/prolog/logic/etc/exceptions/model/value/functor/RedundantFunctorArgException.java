package ru.prolog.logic.etc.exceptions.model.value.functor;

import ru.prolog.logic.etc.exceptions.model.value.ValueStateException;
import ru.prolog.logic.model.values.FunctorValueModel;
import ru.prolog.util.ToStringUtil;

public class RedundantFunctorArgException extends ValueStateException {

    public RedundantFunctorArgException(FunctorValueModel functorValue, int argNum) {
        this(functorValue, argNum,
                String.format("%s argument %s of %s is redundant. Functor %s requires %d arguments.",
                        ToStringUtil.ordinal(argNum),
                        functorValue.getArgs().get(argNum),
                        functorValue,
                        functorValue.getType().getCompoundType().getFunctor(functorValue.getFunctorName()),
                        functorValue.getType().getCompoundType().getFunctor(functorValue.getFunctorName()).getArgTypeNames().size()));
    }

    public RedundantFunctorArgException(FunctorValueModel sender, int argNum, String message) {
        super(sender.getArgs().get(argNum), message);
    }
}
