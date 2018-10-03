package ru.prolog.etc.exceptions.model.value.functor;

import ru.prolog.model.type.Type;
import ru.prolog.model.values.FunctorValueModel;

public class FunctorValueNameException extends FunctorValueException {
    public FunctorValueNameException(FunctorValueModel functor, String message) {
        super(functor, message);
    }

    public Type getType(){
        return getFunctorValue().getType();
    }

    public String getFunctorName(){
        return getFunctorValue().getFunctorName();
    }
}
