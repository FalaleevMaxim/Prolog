package ru.prolog.storage.database.exceptions;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.storage.database.Database;

public class PredicateNotInDatabaseException extends RuntimeException {
    private String predicateName;
    private Database database;
    public PredicateNotInDatabaseException(String predicateName, Database database) {
    }

    public PredicateNotInDatabaseException(String message) {
        super(message);
    }
}
