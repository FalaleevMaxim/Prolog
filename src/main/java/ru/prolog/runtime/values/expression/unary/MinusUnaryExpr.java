package ru.prolog.runtime.values.expression.unary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.expression.ExprValue;
import ru.prolog.runtime.values.simple.SimpleValue;

public class MinusUnaryExpr extends AbstractUnaryExpr {
    public MinusUnaryExpr(ExprValue innerExpr) {
        super("-", innerExpr);
        this.innerExpr = innerExpr;
    }

    @Override
    public Number getContent() {
        checkFreeVariables();
        Number innerVal = innerExpr.getContent();
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
        if (res.getContent() instanceof Integer)
            val = -((Integer) res.getContent());
        else val = -((Double) res.getContent());
        innerExpr.unify(new SimpleValue(res.getType(), val));
    }
}
