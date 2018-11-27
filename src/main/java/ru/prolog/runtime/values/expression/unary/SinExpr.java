package ru.prolog.runtime.values.expression.unary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class SinExpr extends AbstractUnaryExpr {
    public SinExpr(ExprValue innerExpr) {
        super("sin", innerExpr);
    }

    @Override
    public Double getContent() {
        checkFreeVariables();
        return Math.sin(innerExpr.getContent().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
