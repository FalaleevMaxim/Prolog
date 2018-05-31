package ru.prolog.logic.model.type.descriptions;

import ru.prolog.logic.model.AbstractModelObject;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.exceptions.type.FunctorArgTypeNotExistsException;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.util.NameChecker;
import ru.prolog.util.ToStringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents single functor (name and argument types) in compound type.
 */
public class FunctorType extends AbstractModelObject implements Functor{
    public String name;
    private List<String> argTypes;
    private TypeStorage typeStorage;
    private CompoundType compoundType;

    public FunctorType(String name) {
        this.name = name;
        argTypes = new ArrayList<>();
    }

    public FunctorType(String name, List<String> argTypes, TypeStorage typeStorage) {
        this.name = name;
        this.argTypes = argTypes;
        this.typeStorage = typeStorage;
    }

    @Override
    public CompoundType getCompoundType() {
        return compoundType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getArgTypeNames() {
        return argTypes;
    }


    @Override
    public TypeStorage getTypeStorage() {
        return typeStorage;
    }

    @Override
    public void setCompoundType(CompoundType type) {
        this.compoundType = type;
    }

    @Override
    public void setTypeStorage(TypeStorage typeStorage) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.typeStorage = typeStorage;
    }

    @Override
    public void addArgType(String argType){
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        argTypes.add(argType);
    }

    @Override
    public List<Type> getArgTypes() {
        if(getArgTypeNames().isEmpty())
            return Collections.emptyList();
        return getArgTypeNames().stream()
                .map(s -> getTypeStorage().get(s))
                .collect(Collectors.toList());
    }


    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();
        if(!NameChecker.canBeFunctorName(name))
            exceptions.add(new ModelStateException(this, "Name \"" + name + "\" can not be functor name"));
        for(int i=0; i<argTypes.size(); i++){
            Type type = typeStorage.get(argTypes.get(i));
            if(type==null)
                exceptions.add(new FunctorArgTypeNotExistsException(this, argTypes.get(i), i));
        }
        return exceptions;
    }

    @Override
    public void fixIfOk() {
        if(argTypes.isEmpty()) argTypes = Collections.emptyList();
        else argTypes = Collections.unmodifiableList(new ArrayList<>(argTypes));
    }

    @Override
    public String toString() {
        return ToStringUtil.funcToString(name, getArgTypeNames());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctorType)) return false;

        FunctorType that = (FunctorType) o;

        if (!name.equals(that.name)) return false;
        return argTypes.equals(that.argTypes);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + argTypes.hashCode();
        return result;
    }
}
