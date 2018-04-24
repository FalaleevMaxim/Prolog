package ru.prolog.model.program;

import ru.prolog.context.program.BaseProgramContext;
import ru.prolog.model.ModelBuilder;
import ru.prolog.model.ModelObject;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.service.Managers;
import ru.prolog.storage.database.Database;
import ru.prolog.storage.predicates.PredicateStorage;
import ru.prolog.storage.type.TypeStorage;

import java.util.Collections;
import java.util.Map;

public class Program implements ModelObject{
    private TypeStorage typeStorage;
    private PredicateStorage predicateStorage;
    private StatementExecutorRule goal;
    private Predicate defaultGoal; //ToDo: do something with default goal
    private Database databaseClauses;
    private Map<String, Database> namedDatabases;
    private Managers managers;

    private Program(){}

    public boolean goal(){
        BaseProgramContext programContext = new BaseProgramContext(this);
        if(goal!=null)
            return managers.getRuleManager().executable(goal, Collections.emptyList(), programContext).execute();
        return managers.getPredicateManager().executable(defaultGoal, Collections.emptyList(), programContext).execute();
    }

    public TypeStorage getTypeStorage() {
        return typeStorage;
    }

    public PredicateStorage getPredicateStorage() {
        return predicateStorage;
    }

    public StatementExecutorRule getGoal() {
        return goal;
    }

    public Predicate getDefaultGoal() {
        return defaultGoal;
    }

    public Database getDatabaseClauses() {
        return databaseClauses;
    }

    public Map<String, Database> getNamedDatabases() {
        return namedDatabases;
    }

    public Managers getManagers() {
        return managers;
    }

    public static class Builder implements ModelBuilder<Program>{


        @Override
        public Program create() {
            return null;
        }
    }
}