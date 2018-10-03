package ru.prolog.runtime.values.expression.binary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class DivRealExpr extends AbstractBinaryExpr {
    public DivRealExpr(ExprValue left, ExprValue right) {
        super("/", left, right);
    }

    @Override
    public Double getValue() {
        checkFreeVariables();
        return left.getValue().doubleValue() / right.getValue().doubleValue();
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
