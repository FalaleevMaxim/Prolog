package ru.prolog.model.predicates.execution.predicate;

import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.values.Value;

import java.util.List;

public abstract class BasePredicateExecutionDecorator implements PredicateExecution{
    PredicateExecution decorated;

    public BasePredicateExecutionDecorator(PredicateExecution decorated) {
        this.decorated = decorated;
    }

    @Override
    public Predicate getPredicate() {
        return decorated.getPredicate();
    }

    @Override
    public List<Value> getArgs() {
        return decorated.getArgs();
    }

    @Override
    public void cut() {
        decorated.cut();
    }

    @Override
    public boolean isCut() {
        return decorated.isCut();
    }
}
