package ru.prolog.storage.database;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.predicates.rule.Rule;

import java.util.Collection;
import java.util.List;

public interface Database {
    /**
     * @return all database predicates
     */
    Collection<Predicate> databasePredicates();

    /**
     * @param predicate database predicate
     * @return List of rules stored in
     */
    List<Rule> getRules(Predicate predicate);

    /**
     * Adds rule to the end of rule list
     */
    void assertz(Rule rule);

    /**
     * Adds rule to the start of rule list
     */
    void asserta(Rule rule);
}
