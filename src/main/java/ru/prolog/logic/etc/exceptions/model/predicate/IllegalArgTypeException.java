package ru.prolog.logic.etc.exceptions.model.predicate;

import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.type.Type;

public class IllegalArgTypeException extends PredicateStateException {
    private final Type argType;
    private final int argNum;

    public IllegalArgTypeException(Predicate sender, Type argType, int argNum) {
        this(sender, argType, argNum, "Illegal argument type in predicate");
    }

    public IllegalArgTypeException(Predicate sender, Type argType, int argNum, String message) {
        super(sender, message);
        this.argType = argType;
        this.argNum = argNum;
    }

    public Type getArgType() {
        return argType;
    }

    public int getArgNum() {
        return argNum;
    }
}
