package ru.prolog.logic.runtime.values.list;

import ru.prolog.logic.etc.exceptions.runtime.WrongTypeException;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.values.ListValueModel;
import ru.prolog.logic.model.values.ValueModel;
import ru.prolog.logic.model.values.VariableModel;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;

import java.util.ArrayList;
import java.util.List;

public class ListValue implements PrologList {
    protected Value head;
    protected Type type;
    protected PrologList tail;

    //empty list
    public ListValue(Type type) {
        this.type = type;
    }

    //PrologList element
    public ListValue(Type listType, Value head) {
        this(listType);
        this.head = head;
    }

    private ListValue(Type listType, Value head, PrologList tail){
        this(listType, head);
        this.tail = tail;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Value getValue() {
        return head;
    }

    @Override
    public boolean unify(Value other) {
        PrologList otherList = (PrologList) other;
        if(other instanceof Variable && ((Variable)other).isFree()) return other.unify(this);
        if(isEmpty() && otherList.isEmpty()) return true;
        if(this.isEmpty() || otherList.isEmpty()) return false;
        if(!head.unify(otherList.head())) return false;
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
        for (PrologList list = this; !list.isEmpty(); list = list.tail()){
            if(list instanceof Variable && ((Variable) list).isFree()){
                model.setTail((VariableModel) list.toModel());
                break;
            }
            model.addElement(list.getValue().toModel());
        }
        return model;
    }

    @Override
    public boolean isEmpty(){
        return head==null;
    }

    @Override
    public boolean isLast() {
        return tail==null || tail.isEmpty();
    }

    @Override
    public Value head(){
        return head;
    }

    @Override
    public PrologList tail(){
        if(tail !=null) return tail;
        if(isEmpty()) return null;//Can not get tail of empty list
        tail = new ListValue(type);//Tail of list with 1 element is empty list
        return tail;
    }

    @Override
    public PrologList join(Value value){
        if(value==null) throw new IllegalArgumentException("Value can not be null");
        if(!type.getListType().equals(value.getType())) throw new WrongTypeException("New list element has different type", type.getListType(), value.getType());
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
