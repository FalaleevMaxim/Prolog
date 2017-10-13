package ru.prolog.model.values;

import ru.prolog.WrongTypeException;
import ru.prolog.model.Type;

public class AbstractValue implements Value{
    protected Type type;
    protected Object value;

    public AbstractValue(Type type){
        this.type = type;
    }

    public AbstractValue(Type type, Object value){
        this.type = type;
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Boolean unify(Value other) {
        if(other.getType()!=type) throw new WrongTypeException("Wrong type of value to unify", type, other.getType());
        return true;
    }
}
