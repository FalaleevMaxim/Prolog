package ru.prolog.logic.etc.exceptions.model.value.functor;

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
