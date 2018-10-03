package ru.prolog.runtime.values.expression;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

import java.util.List;

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
        if(other instanceof Variable && ((Variable) other).isFree())
            return other.unify(this);
        return other.getValue().equals(getValue());
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    protected void checkFreeVariables(){
        List<Variable> variables = innerFreeVariables();
        if(!variables.isEmpty())
            throw new FreeVariableException("Free variable " +variables.get(0)+ " in math expression "+name, variables.get(0));
    }
}