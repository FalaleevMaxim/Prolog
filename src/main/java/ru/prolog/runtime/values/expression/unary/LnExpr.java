package ru.prolog.runtime.values.expression.unary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class LnExpr extends AbstractUnaryExpr {
    public LnExpr(ExprValue innerExpr) {
        super("ln", innerExpr);
    }

    @Override
    public Double getContent() {
        checkFreeVariables();
        return Math.log(innerExpr.getContent().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
