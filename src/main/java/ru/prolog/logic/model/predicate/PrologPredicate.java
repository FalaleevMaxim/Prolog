package ru.prolog.logic.model.predicate;

import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.storage.type.TypeStorage;

import java.util.List;

/**
 * Интерфейс для предикатов, которые определяются в коде на Прологе и используют правила.
 */
public interface PrologPredicate extends Predicate {
    void setTypeStorage(TypeStorage typeStorage);
    List<Rule> getRules();
    void addRule(Rule rule);
}
