package ru.prolog.model.values;

import ru.prolog.WrongTypeException;
import ru.prolog.model.Type;
import ru.prolog.model.predicates.execution.rule.RuleExecution;
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
    public PrologList forContext(RuleExecution context) {
        if(isEmpty()) return this;
        ListValue clone = new ListValue(type);
        if(value != null){
            clone.value = value.forContext(context);
        }
        if(!isLast()){
            clone.next = next.forContext(context);
        }
        return clone;
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
