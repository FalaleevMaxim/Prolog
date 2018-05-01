package ru.prolog.values.expression.binary;

import ru.prolog.values.expression.ExprValue;

public interface BinaryExpr extends ExprValue{
    ExprValue getLeft();
    ExprValue getRight();
}
