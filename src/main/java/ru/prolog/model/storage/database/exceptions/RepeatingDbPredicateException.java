package ru.prolog.model.storage.database.exceptions;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.model.storage.database.DatabaseModel;

public class RepeatingDbPredicateException extends ModelStateException {
    private final DatabasePredicate repeating;
    private final DatabasePredicate first;

    public RepeatingDbPredicateException(DatabaseModel database, DatabasePredicate repeating, DatabasePredicate first) {
        this(database, repeating, first, "Database already contains predicate \""+repeating+"\"");
    }

    public RepeatingDbPredicateException(DatabaseModel database, DatabasePredicate repeating, DatabasePredicate first, String message) {
        super(database, message);
        this.repeating = repeating;
        this.first = first;
    }

    public DatabasePredicate getRepeating() {
        return repeating;
    }

    public DatabasePredicate getFirst() {
        return first;
    }
}
