package ru.prolog.logic.etc.exceptions.model.value.functor;

import ru.prolog.logic.etc.exceptions.model.value.ValueStateException;
import ru.prolog.logic.model.values.FunctorValueModel;

public class FunctorValueException extends ValueStateException {
    public FunctorValueException(FunctorValueModel functor, String message) {
        super(functor, message);
    }

    public FunctorValueModel getFunctorValue(){
        return (FunctorValueModel) sender;
    }
}
