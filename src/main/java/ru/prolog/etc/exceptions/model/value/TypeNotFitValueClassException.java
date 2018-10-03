package ru.prolog.etc.exceptions.model.value;

import ru.prolog.model.values.ValueModel;

public class TypeNotFitValueClassException extends ValueStateException{
    public TypeNotFitValueClassException(ValueModel sender, String message) {
        super(sender, message);
    }
}
