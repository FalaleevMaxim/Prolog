package ru.prolog.logic.etc.exceptions.model.rule;

import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.values.ValueModel;

public class WrongRuleArgTypeException extends RuleStateException {
    private final Predicate predicate;
    private final int argNum;

    public WrongRuleArgTypeException(Predicate predicate, Rule rule, int argNum) {
        this(predicate, rule, argNum,
                "Argument type in getRule does not match predicate argument type. " +
                        "Rule argument " + rule.getUnifyArgs().get(argNum) + " is not of type " + predicate.getArgTypeNames().get(argNum));
    }

    public WrongRuleArgTypeException(Predicate predicate, Rule rule, int argNum, String message) {
        super(rule, message);
        this.predicate = predicate;
        this.argNum = argNum;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public ValueModel getRuleArg() {
        return getRule().getUnifyArgs().get(argNum);
    }

    public Type getPredicateExpectedType() {
        return predicate.getTypeStorage().get(getPredicateExpectedTypeName());
    }

    public String getPredicateExpectedTypeName() {
        return predicate.getArgTypeNames().get(argNum);
    }
}
