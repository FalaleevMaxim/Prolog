package ru.prolog.model.type.descriptions;

import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.exceptions.type.CompoundTypeException;

import java.util.*;

/**
 * Type consisting of one or more functors.
 */
public class CompoundType implements ModelObject{
    private Map<String, Functor> functors;
    private boolean fixed = false;

    public CompoundType(){
        this.functors = new HashMap<>();
    }

    public CompoundType(List<Functor> functorList) {
        this.functors = new HashMap<>();
        functorList.forEach(f -> functors.put(f.getName(), f));
    }

    public Map<String, Functor> getFunctors() {
        return Collections.unmodifiableMap(functors);
    }

    public Functor getFunctor(String name){
        return functors.get(name);
    }

    public Set<String> getFunctorNames(){
        return functors.keySet();
    }

    public void addFunctor(Functor functor){
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        functors.put(functor.getName(), functor);
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        if(functors.isEmpty())
            return Collections.singletonList(new CompoundTypeException(this, "No functors in this compound type."));

        Collection<ModelStateException> exceptions = new ArrayList<>();
        for(Functor f : functors.values()) {
            exceptions.addAll(f.exceptions());
        }
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
        functors.values().forEach(Functor::fix);
        functors = Collections.unmodifiableMap(new HashMap<>(functors));
        return this;
    }

    @Override
    public String toString() {
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