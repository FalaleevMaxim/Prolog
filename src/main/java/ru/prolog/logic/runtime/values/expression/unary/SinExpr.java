package ru.prolog.logic.runtime.values.expression.unary;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.runtime.values.expression.ExprValue;

public class SinExpr extends AbstractUnaryExpr {
    public SinExpr(ExprValue innerExpr) {
        super("sin", innerExpr);
    }

    @Override
    public Double getValue() {
        checkFreeVariables();
        return Math.sin(innerExpr.getValue().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
