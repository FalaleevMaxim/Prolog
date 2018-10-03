package ru.prolog.etc.exceptions.model.predicate;

import ru.prolog.model.predicate.Predicate;

public class VarArgNotLastException extends PredicateStateException {
    private final int argNum;

    public VarArgNotLastException(Predicate predicate, int argNum) {
        this(predicate, argNum, Integer.toString(argNum)+"th atgument of predicate " + predicate + " is vararg but is not last");
    }

    public VarArgNotLastException(Predicate predicate, int argNum, String message) {
        super(predicate, message);
        this.argNum = argNum;
    }

    public int getArgNum() {
        return argNum;
    }
}
