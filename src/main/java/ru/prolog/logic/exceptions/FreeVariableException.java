package ru.prolog.logic.exceptions;

import ru.prolog.logic.values.Variable;

public class FreeVariableException extends PrologRuntimeException {
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
