package ru.prolog.values.expression.unary;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.type.Type;
import ru.prolog.values.expression.ExprValue;

public class AbsExpr extends AbstractUnaryExpr{
    public AbsExpr(ExprValue innerExpr) {
        super("abs", innerExpr);
    }

    @Override
    public Number getValue() {
        Number innerVal = innerExpr.getValue();
        if(innerExpr.getType().getPrimitiveType().isReal())
            return Math.abs(innerVal.doubleValue());
        return Math.abs(innerVal.intValue());
    }

    @Override
    public Type getType() {
        return innerExpr.getType();
    }
}