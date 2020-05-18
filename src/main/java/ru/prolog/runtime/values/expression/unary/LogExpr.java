package ru.prolog.runtime.values.expression.unary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class LogExpr extends AbstractUnaryExpr {
    public LogExpr(ExprValue innerExpr) {
        super("log", innerExpr);
    }

    @Override
    public Double getContent() {
        checkFreeVariables();
        return Math.log10(innerExpr.getContent().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
