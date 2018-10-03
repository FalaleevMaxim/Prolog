package ru.prolog.etc.exceptions.model.predicate;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.predicate.Predicate;

public class PredicateStateException extends ModelStateException{
    public PredicateStateException(Predicate predicate, String message) {
        super(predicate, message);
    }

    public Predicate getPredicate(){
        return (Predicate) sender;
    }
}
