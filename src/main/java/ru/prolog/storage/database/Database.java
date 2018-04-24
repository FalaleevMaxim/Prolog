package ru.prolog.storage.database;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.FactRule;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Database {
    /**
     * @return all database predicates
     */
    Collection<Predicate> databasePredicates();

    boolean contains(Predicate predicate);

    /**
     * @return true if database contains predicate with given functorName
     */
    boolean contains(String predicateName);

    /**
     * @param predicate database predicate
     * @return List of rules stored in
     */
    List<FactRule> getRules(Predicate predicate);

    /**
     * @return all contents of database
     */
    Map<Predicate, List<FactRule>> allFacts();

    /**
     * Adds rule to the end of rule list
     * @throws ru.prolog.storage.database.exceptions.AssertFreeVariableException if rule contains free variable
     */
    void assertz(FactRule rule);

    /**
     * Adds rule to the start of rule list
     * @throws ru.prolog.storage.database.exceptions.AssertFreeVariableException if rule contains free variable
     */
    void asserta(FactRule rule);

    /**
     * Unifies given fact with facts in database and removes first match
     */
    void retract(FactRule fact, RuleContext context);//ToDO: do something with context

    /**
     * Unifies given fact with facts in database and removes all matches.
     * @throws ru.prolog.exceprions.FreeVariableException if fact contains free variable (except anonymous variables) ToDO make special ecxeption
     */
    void retractAll(FactRule fact);
}
