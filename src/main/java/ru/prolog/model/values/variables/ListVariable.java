package ru.prolog.model.values.variables;

import ru.prolog.WrongTypeException;
import ru.prolog.model.Type;
import ru.prolog.model.values.ListValue;
import ru.prolog.model.values.Value;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ListVariable extends ListValue implements Variable {
    //В отличие от ListValue, null значение value может означать как пустой список, так и отсутствие значения переменной
    //isEmpty true означает, что переменная содержит пустой список.
    private boolean isEmpty;
    private Set<ListVariable> related;

    public ListVariable(Type type) {
        super(type);
    }

    @Override
    public Value head(){
        return value;
    }

    @Override
    public Boolean unify(Value other) {
        if(other.getType()!=type) throw new WrongTypeException("Wrong type of value to unify", type, other.getType());
        if(!isFree()) return super.unify(other);
        if(other instanceof ListVariable){
            ListVariable listVariable = (ListVariable)other;
            if(listVariable.isFree()){
                addRelated(listVariable);
                return true;
            }
        }
        applyValue(other);
        return true;
    }

    @Override
    public Set<ListVariable> getRelated() {
        return related==null? Collections.emptySet():related;
    }

    @Override
    public void addRelated(Variable variable) {
        if(variable.getType()!=getType())
            throw new WrongTypeException("Wrong type of variable to add as related", type, variable.getType());
        if(related==null) related = new HashSet<>();
        related.add((ListVariable) variable);
        if(!variable.isRelated(this)) variable.addRelated(this);
    }

    @Override
    public void removeRelated(Variable variable) {
        related.remove(variable);
        if(variable.isRelated(this)) variable.removeRelated(this);
    }

    @Override
    public boolean isRelated(Variable variable) {
        return related.contains(variable);
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public boolean isLast() {
        return next==null;
    }

    @Override
    public void applyValue(Value value) {
        ListValue listValue = (ListValue) value;
        if(listValue.isEmpty()){
            isEmpty = true;
        }else{
            this.value = listValue.getValue();
            if(!listValue.isLast()){
                this.next = listValue.tail();
            }
        }
        if(related!=null){
            for(Variable variable : related){
                if(variable.isFree()){
                    variable.applyValue(value);
                }
            }
        }
    }

    @Override
    public void dismiss() {

    }

    @Override
    public boolean isFree(){
        return !isEmpty && value==null;
    }
}
