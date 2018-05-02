package ru.prolog.model.program;

import ru.prolog.context.program.ProgramContext;
import ru.prolog.managers.Managers;
import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.predicate.GoalPredicate;
import ru.prolog.model.predicate.InnerGoalPredicate;
import ru.prolog.model.rule.StatementExecutorRule;
import ru.prolog.std.InteractiveGoalPredicate;
import ru.prolog.storage.database.DatabaseModel;
import ru.prolog.storage.database.DatabaseModelImpl;
import ru.prolog.storage.predicates.PredicateStorage;
import ru.prolog.storage.predicates.PredicateStorageImpl;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.storage.type.TypeStorageImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Program implements ModelObject{
    private TypeStorage typeStorage;
    private PredicateStorage predicateStorage;
    private GoalPredicate goal;
    private DatabaseModel database;
    private Managers managers;
    private boolean fixed = false;

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
        return managers.getPredicateManager().context(goal, Collections.emptyList(), context).execute();
    }

    public boolean run(){
        return createContext().execute();
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
        Collection<ModelStateException> exceptions = new ArrayList<>();

        exceptions.addAll(typeStorage.exceptions());
        if(!exceptions.isEmpty()) return exceptions;

        exceptions.addAll(database.exceptions());
        exceptions.addAll(predicateStorage.exceptions());
        if(!exceptions.isEmpty()) return exceptions;

        exceptions.addAll(goal.exceptions());
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
        typeStorage.fix();
        database.fix();
        predicateStorage.fix();
        if(goal().getStatements().isEmpty()){
            goal = new InteractiveGoalPredicate();
        }else{
            goal.fix();
        }
        managers.fix();
        return this;
    }

    @Override
    public String toString() {
        return typeStorage.toString()+
                database.toString()+
                predicateStorage.toString()+
                (goal instanceof InnerGoalPredicate? goal.toString() :"");
    }
}