package ru.prolog.logic.storage.predicates;

import ru.prolog.compiler.position.ModelCodeIntervals;
import ru.prolog.logic.model.AbstractModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.type.Type;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SinglePredicateStorage extends AbstractModelObject implements PredicateStorage {
    private final Predicate predicate;
    private final Collection<Predicate> predicateCollection;

    public SinglePredicateStorage(Predicate predicate) {
        this.predicate = predicate;
        this.predicateCollection = Collections.singleton(predicate);
    }

    @Override
    public Collection<Predicate> all() {
        return predicateCollection;
    }

    @Override
    public Collection<Predicate> get(String name) {
        return predicate.getName().equals(name)?predicateCollection:null;
    }

    @Override
    public Predicate get(String name, int arity) {
        return (predicate.getName().equals(name) && predicate.getArity()==arity )?predicate:null;
    }

    @Override
    public Predicate getVarArgPredicate(String name) {
        return get(name, Integer.MAX_VALUE);
    }

    @Override
    public Predicate getFitting(String name, List<Type> types) {
        return predicate.getName().equals(name)?predicate:null;
    }

    @Override
    public void add(Predicate predicate) {
        throw new UnsupportedOperationException("Can not add predicate to single-predicate storage");
    }

    @Override
    public boolean isBuiltInPredicate(Predicate p) {
        return false;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        return predicate.exceptions();
    }

    @Override
    public void fixIfOk() {
        predicate.fix();
    }

    @Override
    public ModelCodeIntervals getCodeIntervals() {
        return null;
    }

    @Override
    public void setCodeIntervals(ModelCodeIntervals pos) {
    }
}
