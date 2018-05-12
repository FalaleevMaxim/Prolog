package ru.prolog.logic.storage.database;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.predicate.DatabasePredicate;

import java.util.Collection;

public interface DatabaseModel extends ModelObject{
    String DEFAULT_DB_NAME = "dbasedom";
    Collection<String> databases();
    Collection<DatabasePredicate> predicates(String databaseName);
    DatabasePredicate getPredicate(String predicateName);
    String databaseName(String predicateName);
    boolean contains(String predicateName);
    boolean contains(String dbName, String predicateName);

    void addDatabase(String name);

    /**
     * Add predicate to default database
     */
    void addPredicate(DatabasePredicate predicate);

    /**
     * add predicate to specified database
     */
    void addPredicate(DatabasePredicate predicate, String dbName);
}
