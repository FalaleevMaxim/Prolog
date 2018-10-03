package ru.prolog.etc.exceptions.model.value;

import ru.prolog.model.values.ValueModel;

public class TypeNotFitValueObjectException extends ValueStateException {
    public TypeNotFitValueObjectException(ValueModel sender, String message) {
        super(sender, message);
    }
}
