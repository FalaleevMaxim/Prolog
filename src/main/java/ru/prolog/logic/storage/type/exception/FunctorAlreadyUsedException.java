package ru.prolog.logic.storage.type.exception;

import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.type.descriptions.Functor;

public class FunctorAlreadyUsedException extends ModelStateException {
    private final Functor previous;

    public FunctorAlreadyUsedException(Functor repeated, Functor previous) {
        super(repeated, "Functor \"" + repeated.getName() +  "\" already used");
        this.previous = previous;

    }

    public Functor getPrevious() {
        return previous;
    }

    public Functor getRepeated() {
        return (Functor) sender;
    }
}
