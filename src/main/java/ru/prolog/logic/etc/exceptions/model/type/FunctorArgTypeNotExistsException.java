package ru.prolog.logic.etc.exceptions.model.type;

import ru.prolog.logic.etc.exceptions.model.ModelStateException;
import ru.prolog.logic.model.type.descriptions.FunctorType;

public class FunctorArgTypeNotExistsException extends ModelStateException{
    private final String argTypeName;
    private final int argNum;

    public FunctorArgTypeNotExistsException(FunctorType functor, String argTypeName, int argNum) {
        this(functor, argTypeName, argNum, "Type \""+ argTypeName + "\" does not exist");
    }

    public FunctorArgTypeNotExistsException(FunctorType functor, String argTypeName, int argNum, String message) {
        super(functor, message);
        this.argTypeName = argTypeName;
        this.argNum = argNum;
    }

    public FunctorType getFunctor(){
        return (FunctorType) sender;
    }

    public String getArgTypeName() {
        return argTypeName;
    }

    public int getArgNum() {
        return argNum;
    }
}
