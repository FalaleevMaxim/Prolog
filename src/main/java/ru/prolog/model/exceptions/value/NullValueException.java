package ru.prolog.model.exceptions.value;

import ru.prolog.values.model.ValueModel;

public class NullValueException extends ValueStateException {
    public NullValueException(ValueModel sender) {
        super(sender, "Value is null");
    }
}
