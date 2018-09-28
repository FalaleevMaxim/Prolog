package ru.prolog.logic.etc.exceptions.model.rule;

import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.model.values.VariableModel;

public class VariableInFactException extends RuleStateException {
    private final VariableModel variable;

    public VariableInFactException(Rule sender, VariableModel variable) {
        this(sender, variable, "Variables are not allowed in facts");
    }

    public VariableInFactException(Rule sender, VariableModel variable, String message) {
        super(sender, message);
        this.variable = variable;
    }

    public VariableModel getVariable() {
        return variable;
    }
}