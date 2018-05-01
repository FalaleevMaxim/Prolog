package ru.prolog.model.predicate;

import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.exceptions.predicate.PredicateArgTypeNotExistsException;
import ru.prolog.model.exceptions.predicate.VarArgNotLastException;
import ru.prolog.model.type.Type;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.util.ToStringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractPredicate implements Predicate {
    protected String name;
    protected List<String> argTypes;
    protected TypeStorage typeStorage;

    protected AbstractPredicate(String name){
        this.name = name;
        this.argTypes = new ArrayList<>();
    }

    public AbstractPredicate(String name, List<String> argTypes, TypeStorage typeStorage) {
        this.name = name;
        this.argTypes = Collections.unmodifiableList(new ArrayList<>(argTypes));
        this.typeStorage = typeStorage;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public List<String> getArgTypeNames(){
        return argTypes;
    }

    @Override
    public TypeStorage getTypeStorage() {
        return typeStorage;
    }

    @Override
    public List<Type> getArgTypes() {
        if(getTypeStorage()==null){
            if(getArgTypeNames().isEmpty())
                return Collections.emptyList();
            else return null;
        }
        return getArgTypeNames().stream()
                .map(s -> getTypeStorage().get(s))
                .collect(Collectors.toList());
    }


    /**
     * Checks that argument types exist in typeStorage
     */
    @Override
    public Collection<ModelStateException> exceptions() {
        List<ModelStateException> exceptions = new ArrayList<>();
        for(int i=0; i<argTypes.size(); i++){
            Type type = typeStorage.get(argTypes.get(i));
            if(type==null)
                exceptions.add(new PredicateArgTypeNotExistsException(this, argTypes.get(i), i));
            else if(type.isVarArg() && i<argTypes.size()-1){
                //VarArg can be only last argument
                exceptions.add(new VarArgNotLastException(this, i));
            }
        }
        return exceptions;
    }

    @Override
    public ModelObject fix() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPredicate)) return false;

        AbstractPredicate that = (AbstractPredicate) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return argTypes != null ? argTypes.equals(that.argTypes) : that.argTypes == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (argTypes != null ? argTypes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringUtil.funcToString(name, argTypes);
    }
}
