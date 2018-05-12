package ru.prolog.logic.values.expression.binary;

import ru.prolog.logic.values.expression.ExprValue;

public class MultiplyExpr extends IntRealExpr {
    public MultiplyExpr(ExprValue left, ExprValue right) {
        super("*", left, right);
    }

    @Override
    protected Double realOperation(double left, double right) {
        return left*right;
    }

    @Override
    protected Integer intOperation(int left, int right) {
        return left*right;
    }
}
