package ru.prolog.logic.values.simple;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.AbstractValue;
import ru.prolog.logic.values.model.ValueModel;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;

import java.util.Collections;
import java.util.List;

public class SimpleValue extends AbstractValue{
    public SimpleValue(Type type, Object value){
        super(type, value);
    }

    @Override
    public boolean unify(Value other) {
        super.unify(other);
        if(other instanceof Variable && ((Variable)other).isFree()){
            return other.unify(this);
        }
        return value.equals(other.getValue());
    }

    @Override
    public List<Variable> innerFreeVariables() {
        return Collections.emptyList();
    }

    @Override
    public ValueModel toModel() {
        return new SimpleValueModel(type, value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
