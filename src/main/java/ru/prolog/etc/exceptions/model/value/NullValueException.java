package ru.prolog.etc.exceptions.model.value;

import ru.prolog.model.values.ValueModel;

public class NullValueException extends ValueStateException {
    public NullValueException(ValueModel sender) {
        super(sender, "Value is null");
    }
}
