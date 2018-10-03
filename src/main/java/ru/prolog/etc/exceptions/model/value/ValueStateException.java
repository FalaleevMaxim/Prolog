package ru.prolog.etc.exceptions.model.value;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.values.ValueModel;

public class ValueStateException extends ModelStateException {
    public ValueStateException(ValueModel sender, String message) {
        super(sender, message);
    }
}
