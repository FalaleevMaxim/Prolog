package ru.prolog.logic.values.list;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.type.exceptions.WrongTypeException;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
import ru.prolog.logic.values.model.ListValueModel;
import ru.prolog.logic.values.model.ValueModel;

import java.util.ArrayList;
import java.util.List;

public class ListValue implements PrologList {
    protected Value value;
    protected Type type;
    protected PrologList next;

    //empty list
    public ListValue(Type type) {
        this.type = type;
    }

    //PrologList element
    public ListValue(Type listType, Value value) {
        this(listType);
        this.value = value;
    }

    private ListValue(Type listType, Value value, PrologList tail){
        this(listType, value);
        this.next = tail;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public boolean unify(Value other) {
        if(other.getType()!=getType()) throw new WrongTypeException("Wrong type of value to unify", type, other.getType());
        PrologList otherList = (PrologList) other;
        if(other instanceof Variable && ((Variable)other).isFree()) return other.unify(this);
        if(isEmpty() && otherList.isEmpty()) return true;
        if(this.isEmpty() || otherList.isEmpty()) return false;
        if(!value.unify(otherList.head())) return false;
        PrologList tail = tail();
        PrologList otherTail = otherList.tail();
        if(tail==null && otherTail==null) return true;
        if(tail==null || otherTail==null) return false;
        return tail.unify(otherTail);
    }

    @Override
    public List<Variable> innerFreeVariables() {
        List<Variable> variables = new ArrayList<>();
        if(head()!=null) variables.addAll(head().innerFreeVariables());
        if(tail()!=null) variables.addAll(tail().innerFreeVariables());
        return variables;
    }

    @Override
    public ValueModel toModel() {
        ListValueModel model = new ListValueModel(type);
        for (PrologList list = this; list.isEmpty(); list = list.tail()){
            model.addElement(list.getValue().toModel());
        }
        return toModel();
    }

    @Override
    public boolean isEmpty(){
        return value==null;
    }

    @Override
    public boolean isLast() {
        return next==null || next.isEmpty();
    }

    @Override
    public Value head(){
        return value;
    }

    @Override
    public PrologList tail(){
        if(next!=null) return next;
        if(isEmpty()) return null;//Can not get tail of empty list
        return new ListValue(type);//Tail of list with 1 element is empty list
    }

    @Override
    public PrologList join(Value value){
        if(value==null) throw new IllegalArgumentException("Value can not be null");
        if(type.getListType() != value.getType()) throw new WrongTypeException("New list element has different type", type.getListType(), value.getType());
        return new ListValue(type, value, this);
    }

    @Override
    public String toString() {
        PrologList list = this;
        StringBuilder sb = new StringBuilder("[");
        while (!list.isEmpty()){
            if(list instanceof Variable && ((Variable)list).isFree()){
                sb.append("|");
                sb.append(list.toString());
            }else{
                if(list!=this) sb.append(',');
                sb.append(list.head().toString());
            }
            list = list.tail();
            if(list==null) break;

        }
        sb.append("]");
        return sb.toString();
    }
}
