package ru.prolog.model.values;

import ru.prolog.WrongTypeException;
import ru.prolog.model.Type;
import ru.prolog.model.values.variables.Variable;

public class ListValue implements PrologList {
    protected Value value;
    protected Type type;
    protected PrologList next;

    //empty list
    public ListValue(Type type) {
        if(!type.isList()) throw new WrongTypeException("Type of AbstractList must be list", null, type);
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
    public Boolean unify(Value other) {
        if(other.getType()!=type) throw new WrongTypeException("Wrong type of value to unify", type, other.getType());
        PrologList otherList = (PrologList) other;
        if(other instanceof Variable && other.getValue()==null) return other.unify(this);
        if(isEmpty() && otherList.isEmpty()) return true;
        if(this.isEmpty() || otherList.isEmpty()) return false;
        if(!value.unify(otherList.head())) return false;
        return tail().unify(otherList.tail());
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
}
