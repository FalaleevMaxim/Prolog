package ru.prolog.logic.model.exceptions.value;

import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.values.model.ValueModel;

public class ValueStateException extends ModelStateException {
    public ValueStateException(ValueModel sender, String message) {
        super(sender, message);
    }
}
