package ru.prolog.model.predicate;

import ru.prolog.model.rule.Rule;
import ru.prolog.storage.type.TypeStorage;

import java.util.List;

/**
 * Interface for predicates which can be defined from prolog code and use rules.
 * NOT for predicates which are implemented in Java and added to Prolog.
 */
public interface PrologPredicate {
    void setName(String name);
    void addArgType(String type);
    void setTypeStorage(TypeStorage typeStorage);
    List<Rule> getRules();
    void addRule(Rule rule);
}
