package ru.prolog.logic.model.type.descriptions;

import ru.prolog.logic.model.AbstractModelObject;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.exceptions.type.CompoundTypeException;

import java.util.*;

/**
 * Type consisting of one or more functors.
 */
public class CompoundType extends AbstractModelObject{
    private Map<String, Functor> functors;

    public CompoundType(){
        this.functors = new HashMap<>();
    }

    public CompoundType(List<Functor> functorList) {
        this.functors = new HashMap<>();
        for (Functor f : functorList) {
            f.setCompoundType(this);
            functors.put(f.getName(), f);
        }
    }

    public Collection<Functor> getFunctors() {
        return functors.values();
    }

    public Functor getFunctor(String name){
        return functors.get(name);
    }

    public Set<String> getFunctorNames(){
        return functors.keySet();
    }

    public void addFunctor(Functor functor){
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        functor.setCompoundType(this);
        functors.put(functor.getName(), functor);
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();/*
        if(functors.isEmpty())
            return Collections.singletonList(new CompoundTypeException(this, "No functors in this compound type."));*/

        Collection<ModelStateException> exceptions = new ArrayList<>();
        for(Functor f : functors.values()) {
            exceptions.addAll(f.exceptions());
        }
        return exceptions;
    }

    @Override
    public void fixIfOk() {
        functors.values().forEach(Functor::fix);
        functors = Collections.unmodifiableMap(new HashMap<>(functors));
    }

    @Override
    public String toString() {
        if(functors.isEmpty()) return "";
        Iterator<Functor> it = functors.values().iterator();
        StringBuilder sb = new StringBuilder(it.next().toString());
        while (it.hasNext()){
            sb.append("; ").append(it.next());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompoundType)) return false;

        CompoundType that = (CompoundType) o;

        return functors.equals(that.functors);
    }

    @Override
    public int hashCode() {
        return functors.hashCode();
    }
}