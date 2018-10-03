package ru.prolog.logic.runtime.values.expression.unary;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.expression.ExprValue;
import ru.prolog.logic.runtime.values.simple.SimpleValue;

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

    @Override
    protected void reverse(Value res) {
        Object val;
        if (res.getValue() instanceof Integer)
            val = -((Integer) res.getValue());
        else val = -((Double) res.getValue());
        innerExpr.unify(new SimpleValue(res.getType(), val));
    }
}
