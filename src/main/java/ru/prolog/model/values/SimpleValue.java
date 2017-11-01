package ru.prolog.model.values;

import ru.prolog.WrongTypeException;
import ru.prolog.model.Type;
import ru.prolog.model.values.variables.SimpleVariable;

public class SimpleValue extends AbstractValue{


    public SimpleValue(Type type, Object value){
        super(type, value);
        if(value==null) throw new IllegalArgumentException("Value can not be null. Use SimpleVariable to contain null value");
        if(type.isList()) throw new WrongTypeException("Use AbstractList for list type.", null, type);
    }

    @Override
    public Boolean unify(Value other) {
        super.unify(other);
        if(other instanceof SimpleVariable && other.getValue()==null){
            return other.unify(this);
        }
        return value.equals(other.getValue());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
