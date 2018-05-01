package ru.prolog.values.expression.unary;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.type.Type;
import ru.prolog.values.expression.ExprValue;

public class SinExpr extends AbstractUnaryExpr {
    public SinExpr(ExprValue innerExpr) {
        super("sin", innerExpr);
    }

    @Override
    public Double getValue() {
        return Math.sin(innerExpr.getValue().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
