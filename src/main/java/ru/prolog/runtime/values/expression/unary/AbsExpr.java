package ru.prolog.runtime.values.expression.unary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class AbsExpr extends AbstractUnaryExpr{
    public AbsExpr(ExprValue innerExpr) {
        super("abs", innerExpr);
    }

    @Override
    public Number getContent() {
        checkFreeVariables();
        Number innerVal = innerExpr.getContent();
        if(innerExpr.getType().getPrimitiveType().isReal())
            return Math.abs(innerVal.doubleValue());
        return Math.abs(innerVal.intValue());
    }

    @Override
    public Type getType() {
        return innerExpr.getType();
    }
}