package ru.prolog.values.expression.unary;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.type.Type;
import ru.prolog.values.expression.ExprValue;

public class CosExpr extends AbstractUnaryExpr{
    public CosExpr(ExprValue innerExpr) {
        super("cos", innerExpr);
    }

    @Override
    public Double getValue() {
        return Math.cos(innerExpr.getValue().doubleValue());
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
