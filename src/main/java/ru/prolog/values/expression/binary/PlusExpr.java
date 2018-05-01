package ru.prolog.values.expression.binary;

import ru.prolog.values.expression.ExprValue;

public class PlusExpr extends IntRealExpr {

    public PlusExpr(ExprValue left, ExprValue right) {
        super("+", left, right);
    }

    @Override
    protected Double realOperation(double left, double right) {
        return left+right;
    }

    @Override
    protected Integer intOperation(int left, int right) {
        return left+right;
    }
}
