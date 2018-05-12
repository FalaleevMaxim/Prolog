package ru.prolog.logic.model.exceptions.value;

import ru.prolog.logic.values.model.ValueModel;

public class NullValueException extends ValueStateException {
    public NullValueException(ValueModel sender) {
        super(sender, "Value is null");
    }
}
