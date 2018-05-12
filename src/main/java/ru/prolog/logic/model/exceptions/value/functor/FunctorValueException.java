package ru.prolog.logic.model.exceptions.value.functor;

import ru.prolog.logic.model.exceptions.value.ValueStateException;
import ru.prolog.logic.values.model.FunctorValueModel;

public class FunctorValueException extends ValueStateException {
    public FunctorValueException(FunctorValueModel functor, String message) {
        super(functor, message);
    }

    public FunctorValueModel getFunctorValue(){
        return (FunctorValueModel) sender;
    }
}
