package ru.prolog.logic.etc.exceptions.model;

import ru.prolog.compiler.CompileException;
import ru.prolog.logic.model.ModelObject;

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
