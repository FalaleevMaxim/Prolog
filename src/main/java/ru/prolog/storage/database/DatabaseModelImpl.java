package ru.prolog.storage.database;

import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.predicate.DatabasePredicate;
import ru.prolog.storage.database.exceptions.RepeatingDbPredicateException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DatabaseModelImpl implements DatabaseModel {
    //dbName->predicateName->predicate
    private Map<String, Map<String,DatabasePredicate>> databases = new HashMap<>();
    //predicateName->dbName
    private Map<String, String> dbNames = new HashMap<>();
    private boolean fixed = false;

    @Override
    public Collection<String> databases() {
        return databases.keySet();
    }

    @Override
    public Collection<DatabasePredicate> predicates(String databaseName) {
        return databases.get(databaseName).values();
    }

    @Override
    public DatabasePredicate getPredicate(String predicateName) {
        String dbName = dbNames.get(predicateName);
        if(dbName==null) return null;
        return databases.get(dbName).get(predicateName);
    }

    @Override
    public String databaseName(String predicateName) {
        return dbNames.get(predicateName);
    }

    @Override
    public boolean contains(String predicateName) {
        return dbNames.containsKey(predicateName);
    }

    @Override
    public boolean contains(String dbName, String predicateName) {
        Map<String, DatabasePredicate> db = databases.get(dbName);
        if(db==null) throw new IllegalArgumentException("Database \""+dbName+"\" not exists");
        return db.containsKey(predicateName);
    }

    @Override
    public void addPredicate(DatabasePredicate predicate) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        addPredicate(predicate, DEFAULT_DB_NAME);
    }

    @Override
    public void addPredicate(DatabasePredicate predicate, String dbName) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        if(dbNames.containsKey(predicate.getName()))
            throw new RepeatingDbPredicateException(this, predicate, getPredicate(predicate.getName()));
        Map<String, DatabasePredicate> db = databases.get(dbName);
        if(db==null) throw new IllegalArgumentException("Database \""+dbName+"\" not exists");
        db.put(predicate.getName(), predicate);
        dbNames.put(predicate.getName(), dbName);
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        return Collections.emptyList();
    }

    @Override
    public ModelObject fix() {
        fixed = true;
        return this;
    }
}
