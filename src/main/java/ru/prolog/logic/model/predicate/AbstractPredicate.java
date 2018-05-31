package ru.prolog.logic.model.predicate;

import ru.prolog.logic.model.AbstractModelObject;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.exceptions.predicate.PredicateArgTypeNotExistsException;
import ru.prolog.logic.model.exceptions.predicate.VarArgNotLastException;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
import ru.prolog.util.ToStringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractPredicate extends AbstractModelObject implements Predicate {
    protected String name;
    protected List<String> argTypes;
    protected TypeStorage typeStorage;

    public AbstractPredicate(String name){
        this.name = name;
        this.argTypes = new ArrayList<>();
    }

    public AbstractPredicate(String name, List<String> argTypes, TypeStorage typeStorage) {
        this.name = name;
        this.argTypes = new ArrayList<>(argTypes);
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
        if(getArgTypeNames().isEmpty())
            return Collections.emptyList();
        return getArgTypeNames().stream()
                .map(s -> getTypeStorage().get(s))
                .collect(Collectors.toList());
    }

    @Override
    public int getArity(){
        if(getArgTypes().isEmpty()) return 0;
        if(getArgTypes().get(getArgTypes().size()-1).isVarArg()) return Integer.MAX_VALUE;
        return getArgTypes().size();
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
    public void fixIfOk() {
        argTypes = Collections.unmodifiableList(argTypes);
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

    protected boolean isFreeVariable(Value arg){
        return arg instanceof Variable && ((Variable) arg).isFree();
    }
}
