package ru.prolog.logic.values.expression.unary;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.expression.ExprValue;

public class AbsExpr extends AbstractUnaryExpr{
    public AbsExpr(ExprValue innerExpr) {
        super("abs", innerExpr);
    }

    @Override
    public Number getValue() {
        checkFreeVariables();
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