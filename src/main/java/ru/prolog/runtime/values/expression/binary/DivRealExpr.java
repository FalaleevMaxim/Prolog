package ru.prolog.runtime.values.expression.binary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class DivRealExpr extends AbstractBinaryExpr {
    public DivRealExpr(ExprValue left, ExprValue right) {
        super("/", left, right);
    }

    @Override
    public Double getContent() {
        checkFreeVariables();
        return left.getContent().doubleValue() / right.getContent().doubleValue();
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
