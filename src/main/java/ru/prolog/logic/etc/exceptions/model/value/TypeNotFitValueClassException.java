package ru.prolog.logic.etc.exceptions.model.value;

import ru.prolog.logic.model.values.ValueModel;

public class TypeNotFitValueClassException extends ValueStateException{
    public TypeNotFitValueClassException(ValueModel sender, String message) {
        super(sender, message);
    }
}
