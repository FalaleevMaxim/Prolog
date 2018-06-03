package ru.prolog.logic.values.expression.binary;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.expression.ExprValue;

public abstract class IntRealExpr extends AbstractBinaryExpr {
    public IntRealExpr(String name, ExprValue left, ExprValue right) {
        super(name, left, right);
    }

    @Override
    //If one of the operands is real, returns real. Otherwise returns integer
    public Type getType() {
        if(left.getType().getPrimitiveType().isReal())
            return Type.primitives.get("real");
        if(right.getType().getPrimitiveType().isReal())
            return Type.primitives.get("real");
        return Type.primitives.get("integer");
    }

    @Override
    public Number getValue() {
        checkFreeVariables();
        Number lVal = left.getValue();
        Number rVal = right.getValue();
        if(left.getType().getPrimitiveType().isReal() || right.getType().getPrimitiveType().isReal()){
            double l = lVal.doubleValue();
            double r = rVal.doubleValue();
            return realOperation(l,r);
        }else{
            int l = lVal.intValue();
            int r = rVal.intValue();
            return intOperation(l,r);
        }
    }

    protected abstract Double realOperation(double left, double right);

    protected abstract Integer intOperation(int left, int right);
}
