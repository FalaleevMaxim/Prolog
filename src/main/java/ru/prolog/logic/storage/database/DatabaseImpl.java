package ru.prolog.logic.storage.database;

import ru.prolog.logic.model.predicate.DatabasePredicate;
import ru.prolog.logic.model.rule.FactRule;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.storage.database.exceptions.PredicateNotInDatabaseException;

import java.util.*;

public class DatabaseImpl implements Database {
    private DatabaseModel model;
    //dbName->predicate->rules
    private Map<String, Map<DatabasePredicate,List<FactRule>>> databases = new HashMap<>();

    public DatabaseImpl(DatabaseModel model) {
        this.model = model;
        for(String dbName : model.databases()){
            //Create databases
            Map<DatabasePredicate,List<FactRule>> db = new HashMap<>();
            databases.put(dbName, db);
            for(DatabasePredicate predicate : model.predicates(dbName)){
                //Create list of rules for each predicate
                List<FactRule> rules = new ArrayList<>();
                db.put(predicate, rules);
                //Fill list with predicate's rules
                for(Rule rule : predicate.getRules()){
                    rules.add((FactRule) rule);
                }
            }
        }
    }

    @Override
    public Collection<DatabasePredicate> databasePredicates() {
        List<DatabasePredicate> predicates = new ArrayList<>();
        for(Map<DatabasePredicate,List<FactRule>> db : databases.values()){
            predicates.addAll(db.keySet());
        }
        return predicates;
    }

    @Override
    public Collection<DatabasePredicate> databasePredicates(String dbName) {
        List<DatabasePredicate> predicates = new ArrayList<>();
        for (Map<DatabasePredicate,List<FactRule>> db : databases.values()){
            predicates.addAll(db.keySet());
        }
        return predicates;
    }

    @Override
    public Collection<String> databaseNames() {
        return databases.keySet();
    }

    @Override
    public String databaseName(String predicateName) {
        return model.databaseName(predicateName);
    }

    @Override
    public boolean contains(String predicateName) {
        return model.contains(predicateName);
    }

    @Override
    public boolean contains(String dbName, String predicateName) {
        return model.contains(dbName, predicateName);
    }

    @Override
    public DatabasePredicate get(String predicateName) {
        String dbName = databaseName(predicateName);
        if(dbName==null) return null;
        return get(dbName, predicateName);
    }

    @Override
    public DatabasePredicate get(String dbName, String predicateName) {
        Map<DatabasePredicate,List<FactRule>> db = databases.get(dbName);
        if(db==null) return null;
        return db.keySet().stream().filter(p -> p.getName().equals(predicateName)).findFirst().orElse(null);
    }

    @Override
    public List<FactRule> getRules(DatabasePredicate predicate) {
        String dbName = databaseName(predicate.getName());
        if(dbName==null) throw new PredicateNotInDatabaseException(predicate.getName(), this);
        return databases.get(dbName).get(predicate);
    }

    @Override
    public void assertz(FactRule fact) {
        assert_(fact, false);
    }

    @Override
    public void asserta(FactRule fact) {
        assert_(fact, true);
    }

    private void assert_(FactRule fact, boolean a){
        List<FactRule> rules = getRulesList(fact);
        if(a) rules.add(0, fact);
        else rules.add(fact);
    }

    @Override
    public void retract(FactRule fact) {
        List<FactRule> rules = getRulesList(fact);
        rules.remove(fact);
    }

    @Override
    public String save() {
        return save(DEFAULT_DB_NAME);
    }

    @Override
    public String save(String dbName) {
        Map<DatabasePredicate, List<FactRule>> db = databases.get(dbName);
        if(db==null) throw new IllegalArgumentException("Database \""+dbName+"\" not exists");
        if(db.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for(List<FactRule> ruleList : db.values()){
            for (FactRule rule : ruleList){
                sb.append(rule).append("\n");
            }
        }
        return sb.toString();
    }

    private List<FactRule> getRulesList(FactRule fact) {
        fact.fix();//Just in case if it is not fixed and has exceptions
        DatabasePredicate predicate = (DatabasePredicate)fact.getPredicate();
        String dbName = databaseName(predicate.getName());
        if(dbName==null) throw new PredicateNotInDatabaseException(predicate.getName(), this);
        return databases.get(dbName).get(predicate);
    }
}