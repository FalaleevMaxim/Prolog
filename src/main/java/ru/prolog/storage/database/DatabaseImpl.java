package ru.prolog.storage.database;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.variables.backup.Backup;
import ru.prolog.values.variables.backup.RelatedBackup;
import ru.prolog.values.variables.backup.ValueBackup;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.FactRule;
import ru.prolog.storage.database.exceptions.AssertFreeVariableException;
import ru.prolog.storage.database.exceptions.PredicateNotInDatabaseException;
import ru.prolog.values.Value;
import ru.prolog.values.variables.Variable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseImpl implements Database {
    private Map<Predicate, List<FactRule>> database = new HashMap<>();

    @Override
    public Collection<Predicate> databasePredicates() {
        return database.keySet();
    }

    @Override
    public boolean contains(Predicate predicate) {
        return database.containsKey(predicate);
    }

    @Override
    public boolean contains(String predicateName) {
        for(Predicate p : database.keySet()){
            if(p.getName().equals(predicateName))
                return true;
        }
        return false;
    }

    @Override
    public List<FactRule> getRules(Predicate predicate) {
        return database.get(predicate);
    }

    @Override
    public Map<Predicate, List<FactRule>> allFacts() {
        return database;
    }

    @Override
    public void assertz(FactRule rule) {
        Predicate predicate = rule.getPredicate();
        if(!database.containsKey(predicate)) throw new PredicateNotInDatabaseException(predicate, this);
        checkAssertFact(rule);
        database.get(predicate).add(rule);
    }

    @Override
    public void asserta(FactRule fact) {
        Predicate predicate = fact.getPredicate();
        if(!database.containsKey(predicate)) throw new PredicateNotInDatabaseException(predicate, this);
        checkAssertFact(fact);
        database.get(predicate).add(0, fact);
    }

    @Override
    public void retract(FactRule fact, RuleContext context) {
        Predicate predicate = fact.getPredicate();
        if(!database.containsKey(predicate)) throw new PredicateNotInDatabaseException(predicate, this);
        for(FactRule dbFact : database.get(predicate)){
            List<Backup> backups = fact.getUnifyArgs().stream()
                    .filter(value -> value instanceof Variable)
                    .map(value -> new RelatedBackup(new ValueBackup((Variable) value)))//ToDo: use BackupManager
                    .collect(Collectors.toList());
            fact.unifyArgs(dbFact.getUnifyArgs(), context);
        }
    }

    @Override
    public void retractAll(FactRule fact) {
        //ToDo: implement when doing retract ptedicate
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (List<FactRule> facts : database.values()){
            for(FactRule rule : facts){
                sb.append(rule).append('\n');
            }
        }
        return sb.toString();
    }

    private void checkAssertFact(FactRule fact){
        for (Value val : fact.getUnifyArgs()){
            if(val instanceof Variable){
                Variable var = (Variable) val;
                if(var.isFree())
                    throw new AssertFreeVariableException(fact, (Variable) val);
            }
        }
    }
}
