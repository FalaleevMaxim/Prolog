package ru.prolog.model.exceptions.value.functor;

import ru.prolog.model.exceptions.value.ValueStateException;
import ru.prolog.values.model.FunctorValueModel;

public class FunctorValueException extends ValueStateException {
    public FunctorValueException(FunctorValueModel functor, String message) {
        super(functor, message);
    }

    public FunctorValueModel getFunctorValue(){
        return (FunctorValueModel) sender;
    }
}
