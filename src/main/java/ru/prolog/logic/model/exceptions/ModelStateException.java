package ru.prolog.logic.model.exceptions;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.compiler.CompileException;

public class ModelStateException extends CompileException {
    public final ModelObject sender;

    public ModelStateException(ModelObject sender, String message) {
        super(sender.getCodeIntervals().getBaseInterval()!=null?
                    sender.getCodeIntervals().getBaseInterval():
                    sender.getCodeIntervals().getFullInterval(),
                message);
        this.sender = sender;
    }
}
