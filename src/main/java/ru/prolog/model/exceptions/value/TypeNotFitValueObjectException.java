package ru.prolog.model.exceptions.value;

import ru.prolog.values.model.ValueModel;

public class TypeNotFitValueObjectException extends ValueStateException {
    public TypeNotFitValueObjectException(ValueModel sender, String message) {
        super(sender, message);
    }
}
