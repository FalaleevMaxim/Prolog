package ru.prolog.model.storage.type.exception;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.type.descriptions.Functor;

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
