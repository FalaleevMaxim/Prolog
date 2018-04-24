package ru.prolog.storage.database.exceptions;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.storage.database.Database;

//ToDO: implement better
public class PredicateNotInDatabaseException extends RuntimeException {
    private Predicate predicate;
    private Database database;
    public PredicateNotInDatabaseException(Predicate predicate, Database database) {
    }

    public PredicateNotInDatabaseException(String message) {
        super(message);
    }
}
