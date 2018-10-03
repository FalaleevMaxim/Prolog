package ru.prolog.runtime.values.simple;

import ru.prolog.model.type.Type;
import ru.prolog.model.values.SimpleValueModel;
import ru.prolog.model.values.ValueModel;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.util.ToStringUtil;

import java.util.Collections;
import java.util.List;

public class SimpleValue implements Value{
    protected Type type;
    protected Object value;

    public SimpleValue(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean unify(Value other) {
        if(other instanceof Variable && ((Variable)other).isFree()){
            return other.unify(this);
        }
        return value.equals(other.getValue());
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
    public List<Variable> innerFreeVariables() {
        return Collections.emptyList();
    }

    @Override
    public ValueModel toModel() {
        return new SimpleValueModel(type, value);
    }

    @Override
    public String toString() {
        return ToStringUtil.simpleToString(type, value);
    }
}
