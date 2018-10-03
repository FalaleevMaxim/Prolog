package ru.prolog.etc.exceptions.model.value.functor;

import ru.prolog.etc.exceptions.model.value.ValueStateException;
import ru.prolog.model.values.FunctorValueModel;

public class FunctorValueException extends ValueStateException {
    public FunctorValueException(FunctorValueModel functor, String message) {
        super(functor, message);
    }

    public FunctorValueModel getFunctorValue(){
        return (FunctorValueModel) sender;
    }
}
