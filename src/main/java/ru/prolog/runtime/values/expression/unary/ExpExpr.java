package ru.prolog.runtime.values.expression.unary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class ExpExpr extends AbstractUnaryExpr {
    public ExpExpr(ExprValue innerExpr) {
        super("exp", innerExpr);
    }

    @Override
    public Double getContent() {
        checkFreeVariables();
        return Math.exp(innerExpr.getContent().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
