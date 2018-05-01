package ru.prolog.model.exceptions.rule;

import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.rule.Rule;

public class RuleStateException extends ModelStateException {
    public RuleStateException(Rule sender) {
        super(sender);
    }

    public RuleStateException(Rule sender, String message) {
        super(sender, message);
    }

    public Rule getRule(){
        return (Rule) sender;
    }
}
