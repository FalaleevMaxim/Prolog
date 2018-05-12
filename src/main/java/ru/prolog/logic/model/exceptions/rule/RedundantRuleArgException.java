package ru.prolog.logic.model.exceptions.rule;

import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.values.model.ValueModel;

public class RedundantRuleArgException extends RuleStateException{
    private final int argNum;

    public RedundantRuleArgException(Rule sender, int argNum) {
        this(sender, argNum,
                String.format("Rule %dth argument %s is redundant. Predicate %s requires %d arguments.",
                        argNum, sender.getUnifyArgs().get(argNum), sender.getPredicate(), sender.getPredicate().getArgTypeNames().size()));
    }

    public RedundantRuleArgException(Rule sender, int argNum, String message) {
        super(sender, message);
        this.argNum = argNum;
    }

    public ValueModel getArg() {
        return getRule().getUnifyArgs().get(argNum);
    }

    public int getArgNum() {
        return argNum;
    }
}
