package ru.prolog.logic.values.expression.unary;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.expression.ExprValue;

public class TanExpr extends AbstractUnaryExpr{
    public TanExpr(ExprValue innerExpr) {
        super("tan", innerExpr);
    }

    @Override
    public Double getValue() {
        return Math.tan(innerExpr.getValue().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
