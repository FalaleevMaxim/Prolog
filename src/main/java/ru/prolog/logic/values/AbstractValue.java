package ru.prolog.logic.values;

import ru.prolog.logic.model.type.exceptions.WrongTypeException;
import ru.prolog.logic.model.type.Type;

/**
 * Abstract value containing fields and methods to store value and type.
 * Unify method only checks that values are of same type.
 */
public abstract class AbstractValue implements Value{
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
    public boolean unify(Value other) {
        if(other.getType()!=type) throw new WrongTypeException("Wrong type of value to unify", type, other.getType());
        return true;
    }
}
