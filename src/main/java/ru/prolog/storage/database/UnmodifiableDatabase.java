package ru.prolog.storage.database;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.FactRule;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UnmodifiableDatabase implements Database {
    private final Database database;

    public UnmodifiableDatabase(Database database) {
        this.database = database;
    }

    @Override
    public Collection<Predicate> databasePredicates() {
        return database.databasePredicates();
    }

    @Override
    public boolean contains(Predicate predicate) {
        return database.contains(predicate);
    }

    @Override
    public boolean contains(String predicateName) {
        return database.contains(predicateName);
    }

    @Override
    public List<FactRule> getRules(Predicate predicate) {
        return Collections.unmodifiableList(database.getRules(predicate));
    }

    @Override
    public Map<Predicate, List<FactRule>> allFacts() {
        return Collections.unmodifiableMap(database.allFacts());
    }

    @Override
    public void assertz(FactRule rule) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void asserta(FactRule rule) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retract(FactRule fact, RuleContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retractAll(FactRule fact) {
        throw new UnsupportedOperationException();
    }
}
