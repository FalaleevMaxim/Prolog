package ru.prolog.model.values.variables;

import ru.prolog.WrongTypeException;
import ru.prolog.model.Type;
import ru.prolog.model.predicates.execution.rule.RuleExecution;
import ru.prolog.model.values.ListValue;
import ru.prolog.model.values.PrologList;
import ru.prolog.model.values.Value;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ListVariable extends ListValue implements ListVariableInterface {
    //В отличие от ListValue, null значение value может означать как пустой список, так и отсутствие значения переменной
    //isEmpty true означает, что переменная содержит пустой список.
    private boolean isEmpty;
    private Set<Variable> related;
    private String name;

    public ListVariable(Type type, String name) {
        super(type);
        this.name = name;
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
    public PrologList forContext(RuleExecution context) {
        ListVariable clone = (ListVariable) context.getVariable(name,type);
        if(value != null){
            clone.value = value.forContext(context);
        }
        if(!isLast()){
            clone.next = next.forContext(context);
        }
        return clone;
    }

    @Override
    public Set<Variable> getRelated() {
        return related==null? Collections.emptySet():related;
    }

    @Override
    public void addRelated(Variable variable) {
        if(related != null && related.contains(variable)) return;
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
    public void setFree() {
        value = null;
        isEmpty = false;
        next = null;
    }

    @Override
    public boolean isRelated(Variable variable) {
        return related != null && related.contains(variable);
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
    public boolean isFree(){
        return !isEmpty && value==null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if(isFree()) return name;
        return super.toString();
    }
}
