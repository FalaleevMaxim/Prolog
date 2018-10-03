package ru.prolog.etc.exceptions.model.value;

import ru.prolog.model.type.Type;
import ru.prolog.model.values.VariableModel;

public class WrongVariableTypeException extends ValueStateException{
    private final Type expectedType;

    public WrongVariableTypeException(VariableModel variable, Type expectedType) {
        this(variable, expectedType, "Variable type does not match expected");
    }

    public WrongVariableTypeException(VariableModel variable, Type expectedType, String message) {
        super(variable, message);
        this.expectedType = expectedType;
    }

    public VariableModel getVariable(){
        return (VariableModel) sender;
    }

    public Type getExpectedType() {
        return expectedType;
    }
}
