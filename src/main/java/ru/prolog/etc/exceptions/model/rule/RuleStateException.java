package ru.prolog.etc.exceptions.model.rule;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.rule.Rule;

public class RuleStateException extends ModelStateException {
    public RuleStateException(Rule sender, String message) {
        super(sender, message);
    }

    public Rule getRule(){
        return (Rule) sender;
    }
}
