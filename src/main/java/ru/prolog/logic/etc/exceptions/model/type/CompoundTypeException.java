package ru.prolog.logic.etc.exceptions.model.type;

import ru.prolog.logic.etc.exceptions.model.ModelStateException;
import ru.prolog.logic.model.type.descriptions.CompoundType;

public class CompoundTypeException extends ModelStateException {
    public CompoundTypeException(CompoundType compoundType, String message) {
        super(compoundType, message);
    }

    public CompoundType getCompoundType(){
        return (CompoundType) sender;
    }
}
