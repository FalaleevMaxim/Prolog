package ru.prolog.logic.model.exceptions.value;

import ru.prolog.logic.values.model.ValueModel;

public class TypeNotFitValueObjectException extends ValueStateException {
    public TypeNotFitValueObjectException(ValueModel sender, String message) {
        super(sender, message);
    }
}
