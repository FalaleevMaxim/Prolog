package ru.prolog.logic.runtime.values.expression;

import ru.prolog.logic.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.logic.etc.exceptions.runtime.FreeVariablesException;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;

import java.util.List;

public abstract class AbstractExprValue implements ExprValue {
    protected String name;

    public AbstractExprValue(String name) {
        this.name = name;
    }

    protected void reverse(Value res) {
        throw new FreeVariableException("Operator " + name + " does not support reverse evaluation", innerFreeVariables().get(0));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean unify(Value other) {
        //Если унифицируем со свободной переменной, унифицируем переменную со своим значением.
        //При этом в выражении не должно быть свободных переменных.
        if (other instanceof Variable && ((Variable) other).isFree()) {
            checkFreeVariables();
            return other.unify(this);
        }
        //Если унифицируем со значением, есть несколько вариантов, в зависимости от свободных переменных в выражении:
        List<Variable> freeVars = innerFreeVariables();
        int freeCount = freeVars.size();
        //Если свободных переменных нет, сравниваем значения
        if (freeCount == 0)
            return other.getValue().equals(getValue());
        //Если одна переменная, то пытаемся найти её значение из результата выражение (с которым унифицируемся)
        if (freeCount == 1) {
            reverse(other);
            return true;
        }
        //Если свободных переменных больше 1, исключение.
        throw new FreeVariablesException(freeVars);
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