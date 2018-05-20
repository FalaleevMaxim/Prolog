package ru.prolog.logic.model.exceptions.value.functor;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.values.FunctorValueModel;

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
