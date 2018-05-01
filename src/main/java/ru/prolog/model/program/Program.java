package ru.prolog.model.program;

import ru.prolog.context.program.BaseProgramContext;
import ru.prolog.model.ModelBuilder;
import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.service.Managers;
import ru.prolog.storage.database.Database;
import ru.prolog.storage.database.DatabaseModel;
import ru.prolog.storage.predicates.PredicateStorage;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.storage.type.TypeStorageImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class Program implements ModelObject{
    private TypeStorage typeStorage;
    private PredicateStorage predicateStorage;
    private StatementExecutorRule goal;
    private Predicate defaultGoal; //ToDo: do something with default goal
    private DatabaseModel database;
    private Map<String, Database> namedDatabases;
    private Managers managers;
    private boolean fixed = false;

    private Program(){
        typeStorage = new TypeStorageImpl();
        //predicateStorage =
    }

    public boolean goal(){
        BaseProgramContext programContext = new BaseProgramContext(this);
        if(goal!=null)
            return managers.getRuleManager().context(goal, Collections.emptyList(), programContext).execute();
        return managers.getPredicateManager().context(defaultGoal, Collections.emptyList(), programContext).execute();
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

    public DatabaseModel getDatabase() {
        return database;
    }

    public Map<String, Database> getNamedDatabases() {
        return namedDatabases;
    }

    public Managers getManagers() {
        return managers;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();
        exceptions.addAll(typeStorage.exceptions());
        if(!exceptions.isEmpty())
            return exceptions;
        //ToDo: more exceptions
        return exceptions;
    }

    @Override
    public ModelObject fix() {
        if(fixed) return this;
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty()){
            throw exceptions.iterator().next();
        }
        fixed = true;
        return this;
    }
}