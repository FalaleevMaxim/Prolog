package ru.prolog.runtime.values.expression;

import ru.prolog.model.type.Type;
import ru.prolog.model.values.ExprValueModel;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

import java.util.List;

public class ValueExpr extends AbstractExprValue {
    private final Value value;

    public ValueExpr(Value value) {
        super("");
        this.value = value;
    }

    @Override
    public Number getContent() {
        return (Number) value.getContent();
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

    @Override
    protected void reverse(Value res) {
        value.unify(res);
    }
}
