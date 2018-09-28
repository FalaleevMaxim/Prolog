package ru.prolog.logic.runtime.values.expression.unary;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.runtime.values.expression.ExprValue;

public class MinusUnaryExpr extends AbstractUnaryExpr {
    public MinusUnaryExpr(ExprValue innerExpr) {
        super("-", innerExpr);
        this.innerExpr = innerExpr;
    }

    @Override
    public Number getValue() {
        checkFreeVariables();
        Number innerVal = innerExpr.getValue();
        if(innerExpr.getType().getPrimitiveType().isReal())
            return -innerVal.doubleValue();
        return -innerVal.intValue();
    }

    @Override
    public Type getType() {
        return innerExpr.getType();
    }
}