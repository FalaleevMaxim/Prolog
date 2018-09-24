package ru.prolog.logic.etc.exceptions.model.value;

import ru.prolog.logic.etc.exceptions.model.ModelStateException;
import ru.prolog.logic.model.values.ValueModel;

public class ValueStateException extends ModelStateException {
    public ValueStateException(ValueModel sender, String message) {
        super(sender, message);
    }
}
