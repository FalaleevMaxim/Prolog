package ru.prolog.logic.storage.database.exceptions;

import ru.prolog.logic.storage.database.Database;

public class PredicateNotInDatabaseException extends RuntimeException {
    private String predicateName;
    private Database database;
    public PredicateNotInDatabaseException(String predicateName, Database database) {
    }

    public PredicateNotInDatabaseException(String message) {
        super(message);
    }
}
