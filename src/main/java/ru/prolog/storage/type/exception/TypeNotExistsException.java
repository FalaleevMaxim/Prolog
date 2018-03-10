package ru.prolog.storage.type.exception;

public class TypeNotExistsException extends TypeNameException{
    public TypeNotExistsException(String typename) {
        super(typename, "Type " + typename + " does not exist");
    }

    public TypeNotExistsException(String typeName, String message) {
        super(typeName, message);
    }
}
