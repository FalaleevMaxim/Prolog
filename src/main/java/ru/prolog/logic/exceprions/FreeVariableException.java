package ru.prolog.logic.exceprions;

import ru.prolog.logic.values.Variable;

public class FreeVariableException extends RuntimeException {
    private Variable freeVariable;

    public FreeVariableException(Variable freeVariable) {
        super("Free variable " + freeVariable.getName() + " not allowed here");
        this.freeVariable = freeVariable;
    }

    public FreeVariableException(String message, Variable freeVariable) {
        super(message);
        this.freeVariable = freeVariable;
    }

    public Variable getFreeVariable() {
        return freeVariable;
    }
}
