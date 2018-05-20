package ru.prolog.logic.model.exceptions.rule;

import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.rule.Rule;

public class RuleStateException extends ModelStateException {
    public RuleStateException(Rule sender, String message) {
        super(sender, message);
    }

    public Rule getRule(){
        return (Rule) sender;
    }
}
