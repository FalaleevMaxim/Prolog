package ru.prolog.logic.storage.type.exception;

public abstract class TypeNameException extends RuntimeException {
    private final String typeName;

    public String getTypeName(){
        return typeName;
    }

    public TypeNameException(String typeName, String message) {
        super(message);
        this.typeName = typeName;
    }
}
