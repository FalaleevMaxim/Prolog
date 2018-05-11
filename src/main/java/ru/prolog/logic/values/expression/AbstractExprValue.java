package ru.prolog.logic.values.expression;

import ru.prolog.logic.values.Value;

public abstract class AbstractExprValue implements ExprValue {
    protected String name;

    public AbstractExprValue(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean unify(Value other) {
        if(other.getValue()==null)
            return other.unify(this);
        return other.getValue().equals(getValue());
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}