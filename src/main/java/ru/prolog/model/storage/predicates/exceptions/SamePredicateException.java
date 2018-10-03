package ru.prolog.model.storage.predicates.exceptions;

import ru.prolog.etc.exceptions.model.predicate.PredicateStateException;
import ru.prolog.model.predicate.Predicate;

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
