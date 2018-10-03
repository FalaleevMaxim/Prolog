package ru.prolog.etc.exceptions.model.type;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.type.descriptions.CompoundType;

public class CompoundTypeException extends ModelStateException {
    public CompoundTypeException(CompoundType compoundType, String message) {
        super(compoundType, message);
    }

    public CompoundType getCompoundType(){
        return (CompoundType) sender;
    }
}
