package ru.prolog.logic.model.exceptions.predicate;

import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.predicate.Predicate;

public class PredicateStateException extends ModelStateException{
    public PredicateStateException(Predicate predicate) {
        super(predicate);
    }

    public PredicateStateException(Predicate predicate, String message) {
        super(predicate, message);
    }

    public Predicate getPredicate(){
        return (Predicate) sender;
    }
}
