package ru.prolog.logic.values.expression.binary;

import ru.prolog.logic.values.expression.ExprValue;

public interface BinaryExpr extends ExprValue{
    ExprValue getLeft();
    ExprValue getRight();
}
