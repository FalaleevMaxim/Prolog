package ru.prolog.storage.database.exceptions;

import ru.prolog.exceprions.FreeVariableException;
import ru.prolog.model.rule.FactRule;
import ru.prolog.values.variables.Variable;

/**
 * Exceprion when asserting fact containing free variable
 */
public class AssertFreeVariableException extends FreeVariableException {
    private FactRule rule;
    private Variable variable;

    public AssertFreeVariableException(FactRule rule, Variable variable) {
        super("Asserting free variable " + variable.getName() + " in rule " + rule, variable);
        this.rule = rule;
        this.variable = variable;
    }

    public Variable getVariable() {
        return variable;
    }

    public FactRule getRule() {
        return rule;
    }
}
