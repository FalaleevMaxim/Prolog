package ru.prolog.logic.model.exceptions;

import ru.prolog.logic.model.ModelObject;

public class ModelStateException extends RuntimeException {
    public final ModelObject sender;

    public ModelStateException(ModelObject sender) {
        this.sender = sender;
    }

    public ModelStateException(ModelObject sender, String message) {
        super(message);
        this.sender = sender;
    }
}
