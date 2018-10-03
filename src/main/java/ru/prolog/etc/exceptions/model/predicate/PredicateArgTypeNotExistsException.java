package ru.prolog.etc.exceptions.model.predicate;

import ru.prolog.model.predicate.Predicate;

public class PredicateArgTypeNotExistsException extends PredicateStateException {
    private final String argTypeName;
    private final int argNum;

    public PredicateArgTypeNotExistsException(Predicate sender, String argTypeName, int argNum) {
        this(sender, argTypeName, argNum, "Type \""+ argTypeName + "\" does not exist");
    }

    public PredicateArgTypeNotExistsException(Predicate sender, String argTypeName, int argNum, String message) {
        super(sender, message);
        this.argTypeName = argTypeName;
        this.argNum = argNum;
    }

    public String getArgTypeName() {
        return argTypeName;
    }

    public int getArgNum() {
        return argNum;
    }
}
