package ru.prolog.storage.type.exception;

public class TypeAlreadyExistsException extends TypeNameException {
    public TypeAlreadyExistsException(String typeName) {
        super(typeName, "Type " + typeName + " already exists");
    }

    public TypeAlreadyExistsException(String message, String typeName) {
        super(typeName, message);
    }
}
