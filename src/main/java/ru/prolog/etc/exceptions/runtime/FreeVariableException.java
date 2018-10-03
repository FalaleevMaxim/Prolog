package ru.prolog.etc.exceptions.runtime;

import ru.prolog.runtime.values.Variable;

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
