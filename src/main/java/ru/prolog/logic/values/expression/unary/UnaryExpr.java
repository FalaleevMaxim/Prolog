package ru.prolog.logic.values.expression.unary;

import ru.prolog.logic.values.expression.ExprValue;

public interface UnaryExpr extends ExprValue {
    ExprValue getInnerExpr();
    void setInnerExpr(ExprValue innerExpr);
}
