package ru.prolog.logic.storage.predicates.exceptions;

import ru.prolog.logic.model.exceptions.predicate.PredicateStateException;
import ru.prolog.logic.model.predicate.Predicate;

public class SamePredicateException extends PredicateStateException {
    //Predicate defined first.
    private final Predicate first;

    public SamePredicateException(Predicate predicate, Predicate first) {
        this(predicate, first, "Predicate with same name and arity already exists");
    }

    public SamePredicateException(Predicate predicate, Predicate first, String message) {
        super(predicate, message);
        this.first = first;
    }

    public Predicate getFirst() {
        return first;
    }
}
