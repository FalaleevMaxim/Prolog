package ru.prolog.model.storage.database.exceptions;

import ru.prolog.runtime.database.Database;

public class PredicateNotInDatabaseException extends RuntimeException {
    private String predicateName;
    private Database database;
    public PredicateNotInDatabaseException(String predicateName, Database database) {
    }

    public PredicateNotInDatabaseException(String message) {
        super(message);
    }
}
