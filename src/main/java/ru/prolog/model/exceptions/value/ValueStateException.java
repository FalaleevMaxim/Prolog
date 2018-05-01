package ru.prolog.model.exceptions.value;

import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.values.model.ValueModel;

public class ValueStateException extends ModelStateException {
    public ValueStateException(ValueModel sender, String message) {
        super(sender, message);
    }
}
