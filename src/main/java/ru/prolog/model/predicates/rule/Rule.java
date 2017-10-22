package ru.prolog.model.predicates.rule;

import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.predicates.rule.execution.RuleExecution;
import ru.prolog.model.values.Value;

import java.util.List;

public interface Rule {
    Predicate getPredicate();

    /**
     * @param args Arguments sent to rule from parent context
     * @param context context of executing this rule
     * @return true if all arguments unification was successful
     */
    boolean unifyArgs(List<Value> args, RuleExecution context);

    boolean run(RuleExecution context);
}
