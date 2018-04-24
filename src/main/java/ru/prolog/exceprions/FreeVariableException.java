package ru.prolog.exceprions;

import ru.prolog.values.variables.Variable;

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
