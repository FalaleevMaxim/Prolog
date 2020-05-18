package ru.prolog.runtime.values.expression.unary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class SqrtExpr extends AbstractUnaryExpr {
    public SqrtExpr(ExprValue innerExpr) {
        super("sqrt", innerExpr);
    }

    @Override
    public Double getContent() {
        checkFreeVariables();
        return Math.sqrt(innerExpr.getContent().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
