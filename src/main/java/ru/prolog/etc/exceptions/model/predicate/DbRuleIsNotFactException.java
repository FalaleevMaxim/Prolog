package ru.prolog.etc.exceptions.model.predicate;

import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.rule.Rule;

public class DbRuleIsNotFactException extends PredicateStateException {
    private final Rule rule;

    public DbRuleIsNotFactException(DatabasePredicate predicate, Rule rule) {
        this(predicate, rule, "Rule for database predicate is not fact.");
    }

    public DbRuleIsNotFactException(DatabasePredicate predicate, Rule rule, String message) {
        super(predicate, message);
        this.rule = rule;
    }

    public Rule getRule() {
        return rule;
    }
}
