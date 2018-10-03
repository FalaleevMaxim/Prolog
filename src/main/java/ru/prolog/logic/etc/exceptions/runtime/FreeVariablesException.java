package ru.prolog.logic.etc.exceptions.runtime;

import ru.prolog.logic.runtime.values.Variable;

import java.util.Collection;
import java.util.stream.Collectors;

public class FreeVariablesException extends PrologRuntimeException {
    private Collection<Variable> freeVariables;

    public FreeVariablesException(Collection<Variable> freeVariables) {
        super("Multiple free variables " +
                freeVariables.stream().map(Variable::getName).collect(Collectors.toList())
                + " in math expression");
        this.freeVariables = freeVariables;
    }

    public FreeVariablesException(String message, Collection<Variable> freeVariables) {
        super(message);
        this.freeVariables = freeVariables;
    }

    public Collection<Variable> getFreeVariables() {
        return freeVariables;
    }
}
