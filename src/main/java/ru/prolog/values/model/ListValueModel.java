package ru.prolog.values.model;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.exceptions.value.TypeNotFitValueClassException;
import ru.prolog.model.exceptions.value.ValueStateException;
import ru.prolog.model.exceptions.value.WrongListElementTypeException;
import ru.prolog.model.exceptions.value.WrongVariableTypeException;
import ru.prolog.model.type.Type;
import ru.prolog.values.Value;
import ru.prolog.values.list.PrologList;

import java.util.*;

public class ListValueModel implements ValueModel {
    private Type type;
    private List<ValueModel> heads;
    private VariableModel tail;
    private boolean fixed = false;

    public ListValueModel() {
    }

    public ListValueModel(Type type) {
        this.type = type;
        this.heads = new ArrayList<>();
    }

    public ListValueModel(Type type, ValueModel... heads) {
        this.type = type;
        this.heads = new ArrayList<>(Arrays.asList(heads));
    }

    public ListValueModel(Type type, VariableModel tail, ValueModel... heads) {
        this.type = type;
        this.tail = tail;
        this.heads = new ArrayList<>(Arrays.asList(heads));
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();
        if(type==null) exceptions.add(new ValueStateException(this, "Variable type not defined"));
        else if(!type.isList()){
             exceptions.add(new TypeNotFitValueClassException(this, "Type of ListValueModel must be list"));
        }
        if(!exceptions.isEmpty())
            return exceptions;

        for (ValueModel head : heads){
            exceptions.addAll(head.exceptions());
            if(head.getType()!=null && !head.getType().equals(type.getListType())){
                exceptions.add(new WrongListElementTypeException(this, head));
            }
        }
        if(tail!=null){
            //If tail type not set, sets same type of list.
            if(tail.getType()==null){
                tail.setType(type);
            }else if(!tail.getType().equals(type)){
                exceptions.add(new WrongVariableTypeException(tail, type, "Type of list tail does not match type of list."));
                return exceptions;
            }
            exceptions.addAll(tail.exceptions());
            if(heads.isEmpty()){
                exceptions.add(new ValueStateException(this, "List has tail, but no heads. It does not make sense."));
            }
        }
        return exceptions;
    }

    @Override
    public ModelObject fix() {
        if(fixed) return this;
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty()){
            throw exceptions.iterator().next();
        }
        fixed = true;
        heads.forEach(ValueModel::fix);
        heads = Collections.unmodifiableList(new ArrayList<>(heads));
        if(tail!=null) tail.fix();
        return this;
    }

    @Override
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.type = type;
    }

    public void addElement(ValueModel element){
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        heads.add(element);
    }

    public void setTail(VariableModel tail) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.tail = tail;
    }

    @Override
    public PrologList forContext(RuleContext context) {
        if(!fixed) throw new IllegalStateException("State is not fixed. Call fix() method before using model object.");
        if(tail==null) return PrologList.asList(type, (Value[])heads.toArray());
        return  PrologList.asList((PrologList) tail.forContext(context), (Value[])heads.toArray());
    }

    @Override
    public List<VariableModel> innerModelVariables() {
        List<VariableModel> variables = new ArrayList<>();
        for (ValueModel head : heads){
            variables.addAll(head.innerModelVariables());
        }
        if(tail!=null)
            variables.addAll(tail.innerModelVariables());
        return variables;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if(!heads.isEmpty()) sb.append(heads.get(0));
        for (int i=1; i<heads.size(); i++){
            sb.append(", ").append(heads.get(i));
        }
        if(tail!=null){
            sb.append('|').append(tail);
        }
        sb.append(']');
        return sb.toString();
    }
}
