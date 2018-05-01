package ru.prolog.model.exceptions.type;

import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.type.descriptions.CompoundType;

public class CompoundTypeException extends ModelStateException {
    public CompoundTypeException(CompoundType compoundType, String message) {
        super(compoundType, message);
    }

    public CompoundType getCompoundType(){
        return (CompoundType) sender;
    }
}
