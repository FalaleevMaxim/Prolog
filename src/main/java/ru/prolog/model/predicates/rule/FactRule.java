package ru.prolog.model.predicates.rule;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.values.Value;

import java.util.List;

/**
 * Rule with no body.
 * All methods already implemented in {@link Rule Rule} class.
 * This class is made to separate facts from rules with body because facts have different purposes and rules do not extend them. For example, only facts are allowed to be put in database.
 */
public class FactRule extends Rule {
    public FactRule(Predicate predicate, List<Value> toUnificateList) {
        super(predicate, toUnificateList);
    }
}
