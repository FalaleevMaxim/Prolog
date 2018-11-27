package ru.prolog.runtime.values.expression.unary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class CosExpr extends AbstractUnaryExpr{
    public CosExpr(ExprValue innerExpr) {
        super("cos", innerExpr);
    }

    @Override
    public Double getContent() {
        checkFreeVariables();
        return Math.cos(innerExpr.getContent().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
