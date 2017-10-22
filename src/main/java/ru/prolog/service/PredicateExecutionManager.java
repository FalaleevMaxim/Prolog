package ru.prolog.service;

import ru.prolog.model.predicates.execution.predicate.BasePredicateExecution;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.values.Value;

import java.util.List;

public interface PredicateExecutionManager {
    BasePredicateExecution executable(Rule rule, List<Value> args);

    interface PredicateExecutableOption{

    }
}
