package ru.prolog.logic.etc.exceptions.model.value;

import ru.prolog.logic.model.values.ValueModel;

public class TypeNotFitValueObjectException extends ValueStateException {
    public TypeNotFitValueObjectException(ValueModel sender, String message) {
        super(sender, message);
    }
}
