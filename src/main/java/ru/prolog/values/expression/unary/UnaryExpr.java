package ru.prolog.values.expression.unary;

import ru.prolog.values.expression.ExprValue;

public interface UnaryExpr extends ExprValue {
    ExprValue getInnerExpr();
    void setInnerExpr(ExprValue innerExpr);
}
