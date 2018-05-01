package ru.prolog.model.exceptions.rule;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.Rule;
import ru.prolog.model.type.Type;

public class MissingRuleArgException extends RuleStateException {
    private final Predicate predicate;
    private final int argNum;

    public MissingRuleArgException(Rule sender, Predicate predicate, int argNum) {
        this(sender, predicate, argNum,
                String.format("Predicate %s requires %dth argument of type %s",
                        predicate, argNum, predicate.getArgTypeNames().get(argNum)));
    }

    public MissingRuleArgException(Rule sender, Predicate predicate, int argNum, String message) {
        super(sender, message);
        this.predicate = predicate;
        this.argNum = argNum;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public int getArgNum() {
        return argNum;
    }

    public String getPredicateArgTypeName(){
        return predicate.getArgTypeNames().get(argNum);
    }

    public Type getPredicateArgType(){
        return predicate.getTypeStorage().get(getPredicateArgTypeName());
    }
}
