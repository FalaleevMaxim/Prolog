package ru.prolog.logic.values.expression;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
import ru.prolog.logic.values.model.ExprValueModel;

import java.util.List;

public class ValueExpr extends AbstractExprValue {
    private final Value value;

    public ValueExpr(Value value) {
        super("");
        this.value = value;
    }

    @Override
    public Number getValue() {
        return (Number)value.getValue();
    }

    @Override
    public ExprValueModel toModel() {
        return new ExprValueModel(value.toModel());
    }

    @Override
    public Type getType() {
        return value.getType();
    }

    @Override
    public List<Variable> innerFreeVariables() {
        return value.innerFreeVariables();
    }
}
