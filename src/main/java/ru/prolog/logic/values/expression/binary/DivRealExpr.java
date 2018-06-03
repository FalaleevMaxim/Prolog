package ru.prolog.logic.values.expression.binary;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.expression.ExprValue;

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
