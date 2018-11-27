package ru.prolog.runtime.values.expression.unary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class TanExpr extends AbstractUnaryExpr{
    public TanExpr(ExprValue innerExpr) {
        super("tan", innerExpr);
    }

    @Override
    public Double getContent() {
        checkFreeVariables();
        return Math.tan(innerExpr.getContent().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
