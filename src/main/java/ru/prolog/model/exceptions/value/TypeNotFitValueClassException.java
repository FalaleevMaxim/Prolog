package ru.prolog.model.exceptions.value;

import ru.prolog.values.model.ValueModel;

public class TypeNotFitValueClassException extends ValueStateException{
    public TypeNotFitValueClassException(ValueModel sender, String message) {
        super(sender, message);
    }
}
