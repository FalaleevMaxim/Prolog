package ru.prolog.logic.storage.database;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.predicate.DatabasePredicate;
import ru.prolog.logic.storage.database.exceptions.RepeatingDbPredicateException;

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

    public DatabaseModelImpl() {
        addDatabase(DEFAULT_DB_NAME);
    }

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
        if(!databases.containsKey(dbName)) addDatabase(dbName);
        Map<String, DatabasePredicate> db = databases.get(dbName);
        return db.containsKey(predicateName);
    }

    @Override
    public void addDatabase(String name) {
        databases.put(name, new HashMap<>());
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Map<String,DatabasePredicate> defDb = databases.get(DEFAULT_DB_NAME);
        if(!defDb.isEmpty()){
            sb.append("database\n");
            for(DatabasePredicate p : defDb.values()){
                sb.append('\t').append(p).append('\n');
            }
        }
        for(Map.Entry<String, Map<String,DatabasePredicate>> db : databases.entrySet()){
            if(db.getKey().equals(DEFAULT_DB_NAME)) continue;
            sb.append("database - ").append(db.getKey()).append('\n');
            for(DatabasePredicate p : db.getValue().values()){
                sb.append('\t').append(p).append('\n');
            }
        }
        return sb.toString();
    }
}
