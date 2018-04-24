package ru.prolog.values;

import ru.prolog.model.type.exceptions.WrongTypeException;
import ru.prolog.model.type.Type;
import ru.prolog.values.variables.SimpleVariable;
import ru.prolog.values.variables.Variable;

import java.util.Collections;
import java.util.List;

public class SimpleValue extends AbstractValue{


    public SimpleValue(Type type, Object value){
        super(type, value);
        if(value==null) throw new IllegalArgumentException("Value can not be null. Use SimpleVariable to contain null value");
        if(type.isList()) throw new WrongTypeException("Use AbstractList for list type.", null, type);
    }

    @Override
    public boolean unify(Value other) {
        super.unify(other);
        if(other instanceof SimpleVariable && other.getValue()==null){
            return other.unify(this);
        }
        return value.equals(other.getValue());
    }

    @Override
    public List<Variable> innerVariables() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
