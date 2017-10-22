package ru.prolog.model.predicates.execution.predicate;

import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.values.Value;

import java.util.List;

public interface PredicateExecution {
    boolean execute();
    Predicate getPredicate();
    List<Value> getArgs();

    void cut();
    boolean isCut();
}
