package ru.prolog.model.exceptions.predicate;

import ru.prolog.model.predicate.Predicate;

public class IllegalPredicateNameException extends PredicateStateException {

    public IllegalPredicateNameException(Predicate predicate) {
        this(predicate, "Illegal name for predicate: \""+predicate.getName()+"\"");
    }

    public IllegalPredicateNameException(Predicate predicate, String message) {
        super(predicate, message);
    }
}
