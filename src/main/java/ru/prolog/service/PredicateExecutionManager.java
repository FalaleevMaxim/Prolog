package ru.prolog.service;

import ru.prolog.model.predicates.predicate.PredicateExecution;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.values.Value;

import java.util.List;

public interface PredicateExecutionManager {
    PredicateExecution executable(Rule rule, List<Value> args);

    interface PredicateExecutableOption{

    }
}
