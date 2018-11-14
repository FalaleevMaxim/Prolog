package ru.prolog.model.program;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.AbstractModelObject;
import ru.prolog.model.managers.Managers;
import ru.prolog.model.predicate.GoalPredicate;
import ru.prolog.model.predicate.InnerGoalPredicate;
import ru.prolog.model.predicate.InteractiveGoalPredicate;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.model.storage.database.DatabaseModel;
import ru.prolog.model.storage.database.DatabaseModelImpl;
import ru.prolog.model.storage.predicates.PredicateStorage;
import ru.prolog.model.storage.predicates.PredicateStorageImpl;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.model.storage.type.TypeStorageImpl;
import ru.prolog.runtime.context.program.ProgramContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Program  extends AbstractModelObject implements Runnable {
    private TypeStorage typeStorage;
    private PredicateStorage predicateStorage;
    private GoalPredicate goal;
    private DatabaseModel database;
    private Managers managers;

    public Program(){
        typeStorage = new TypeStorageImpl();
        predicateStorage = new PredicateStorageImpl(typeStorage);
        database = new DatabaseModelImpl();
        managers = new Managers();
        goal = new InnerGoalPredicate();
    }

    public ProgramContext createContext(){
        if(!fixed) throw new IllegalStateException("Program state is not fixed. Call fix() before running it.");
        return managers.getProgramManager().create(this);
    }

    public boolean run(ProgramContext context){
        if(!fixed) throw new IllegalStateException("Program state is not fixed. Call fix() before running it.");
        return managers.getPredicateManager().context(goal, Collections.emptyList(), context).execute().toBoolean();
    }

    public void run(){
        createContext().execute();
    }

    public TypeStorage domains() {
        return typeStorage;
    }

    public PredicateStorage predicates() {
        return predicateStorage;
    }

    public GoalPredicate getGoalPredicate() {
        return goal;
    }

    public StatementExecutorRule goal(){
        if(!(goal instanceof InnerGoalPredicate)) return null;
        return ((InnerGoalPredicate)goal).getGoalRule();
    }

    public DatabaseModel database() {
        return database;
    }

    public Managers managers() {
        return managers;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();

        Collection<ModelStateException> exceptions = new ArrayList<>(typeStorage.exceptions());
        if(!exceptions.isEmpty()) return exceptions;

        exceptions.addAll(database.exceptions());
        exceptions.addAll(predicateStorage.exceptions());
        if(!exceptions.isEmpty()) return exceptions;

        exceptions.addAll(goal.exceptions());
        return exceptions;
    }

    @Override
    public void fixIfOk() {
        typeStorage.fix();
        database.fix();
        predicateStorage.fix();
        if(!goal().hasStatements()){
            goal = new InteractiveGoalPredicate();
        }
        goal.fix();
        managers.fix();
    }

    @Override
    public String toString() {
        return typeStorage.toString()+
                database.toString()+
                predicateStorage.toString()+
                (goal instanceof InnerGoalPredicate? goal.toString() :"");
    }
}