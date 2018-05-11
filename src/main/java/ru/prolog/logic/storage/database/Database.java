package ru.prolog.logic.storage.database;

import ru.prolog.logic.model.predicate.DatabasePredicate;
import ru.prolog.logic.model.rule.FactRule;

import java.util.Collection;
import java.util.List;

public interface Database {
    String DEFAULT_DB_NAME = DatabaseModel.DEFAULT_DB_NAME;

    /**
     * @return all database predicates
     */
    Collection<DatabasePredicate> databasePredicates();

    /**
     * @param dbName name of database
     * @return all predicates in database with specified name. Empty if database does not exist.
     */
    Collection<DatabasePredicate> databasePredicates(String dbName);

    /**
     * @return names of databases
     */
    Collection<String> databaseNames();

    /**
     *
     * @param predicateName name of predicate in database
     * @return name of database containing predicate with given name or null if no database contains such predicate.
     */
    String databaseName(String predicateName);

    /**
     * @param predicateName name of predicate to find in database
     * @return true if any database contains predicate
     */
    boolean contains(String predicateName);

    /**
     * Checks if database with name {@param dbName} contains predicate
     * @param dbName name of database to search predicate
     * @param predicateName name of predicate to search in specified database
     * @return true if predicate found in specified database. False if database does not exist or predicate not found in it.
     */
    boolean contains(String dbName, String predicateName);

    /**
     * Returns database predicate by its name.
     * @param predicateName name of predicate
     * @return predicate by its name or null if predicate not found in database
     */
    DatabasePredicate get(String predicateName);

    /**
     *
     * @param dbName name of database
     * @param predicateName name of predicate
     * @return predicate in specified database by its name or null if predicate not found in database.
     */
    DatabasePredicate get(String dbName, String predicateName);

    /**
     * @param predicate database predicate
     * @return List of rules stored in
     */
    List<FactRule> getRules(DatabasePredicate predicate);

    /**
     * Adds {@param rule} to the end of predicate's rules list
     */
    void assertz(FactRule rule);

    /**
     * Adds getRule to the start of getRule list
     */
    void asserta(FactRule rule);

    /**
     * Removes first occurance of rule in list
     */
    void retract(FactRule fact);

    /**
     * @return all facts of default database as text (each fact on new line)
     */
    String save();

    /**
     * @param dbName name of database
     * @return all facts of specified default database as text (each fact on new line).
     */
    String save(String dbName);
}