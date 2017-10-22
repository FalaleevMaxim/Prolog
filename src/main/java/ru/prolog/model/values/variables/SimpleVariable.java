package ru.prolog.model.values.variables;

import ru.prolog.model.Type;
import ru.prolog.model.values.AbstractValue;
import ru.prolog.model.values.AnonymousVariable;
import ru.prolog.model.values.Value;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Variable of base type
 */
public class SimpleVariable extends AbstractValue implements Variable {
    private Set<Variable> related;
    private String name;

    public SimpleVariable(Type type, String name) {
        super(type);
        this.name = name;
    }

    public boolean isFree(){
        return value==null;
    }

    public Set<Variable> getRelated() {
        return related==null?Collections.emptySet():related;
    }

    @Override
    public Boolean unify(Value other) {
        if(other.getValue() != null){
            if(getValue()!=null){
                return getValue().equals(other.getValue());
            }else{
                applyValue(other);
                return true;
            }
        }else{
            if(other instanceof AnonymousVariable){
                return true;
            }else{
                if(getValue() != null){
                    return other.unify(this);
                }else{
                    Variable variable = (Variable) other;
                    addRelated(variable);
                    return true;
                }
            }
        }
    }

    @Override
    public void applyValue(Value value){
        this.value = value.getValue();
        if(related!=null){
            for(Variable variable : related){
                if(variable.isFree()){
                    //ToDo: apply value only to variables from same context
                    variable.applyValue(value);
                }
            }
        }
    }

    @Override
    public void addRelated(Variable variable){
        if(related==null) related = new HashSet<>();
        related.add(variable);
        if(!variable.isRelated(this)) variable.addRelated(this);
    }

    @Override
    public void removeRelated(Variable variable) {
        related.remove(variable);
        if(variable.isRelated(this)) variable.removeRelated(this);
    }

    @Override
    public boolean isRelated(Variable variable) {
        return related!=null && related.contains(variable);
    }

    @Override
    public void dismiss() {
        Variable first = null;
        //Итератор используется чтобы не получать ConcurrentModificationException
        Iterator<Variable> iterator = related.iterator();
        while (iterator.hasNext()){
            Variable variable = iterator.next();
            if(first==null) first = variable;
            else first.addRelated(variable);
            iterator.remove();
            variable.removeRelated(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
