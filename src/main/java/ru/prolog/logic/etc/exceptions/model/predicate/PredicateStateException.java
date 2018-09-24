package ru.prolog.logic.etc.exceptions.model.predicate;

import ru.prolog.logic.etc.exceptions.model.ModelStateException;
import ru.prolog.logic.model.predicate.Predicate;

public class PredicateStateException extends ModelStateException{
    public PredicateStateException(Predicate predicate, String message) {
        super(predicate, message);
    }

    public Predicate getPredicate(){
        return (Predicate) sender;
    }
}
