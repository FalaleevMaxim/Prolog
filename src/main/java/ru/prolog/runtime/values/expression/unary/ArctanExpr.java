package ru.prolog.runtime.values.expression.unary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class ArctanExpr extends AbstractUnaryExpr {
    public ArctanExpr(ExprValue innerExpr) {
        super("arctan", innerExpr);
    }

    @Override
    public Double getContent() {
        checkFreeVariables();
        return Math.atan(innerExpr.getContent().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
