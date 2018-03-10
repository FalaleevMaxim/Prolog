package ru.prolog.service;

import ru.prolog.context.predicate.BasePredicateContext;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.values.Value;

import java.util.List;

public interface PredicateExecutionManager {
    BasePredicateContext executable(Rule rule, List<Value> args);

    interface PredicateExecutableOption{

    }
}
