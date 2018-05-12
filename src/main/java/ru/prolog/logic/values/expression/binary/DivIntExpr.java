package ru.prolog.logic.values.expression.binary;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.expression.ExprValue;

public class DivIntExpr extends AbstractBinaryExpr{
    public DivIntExpr(ExprValue left, ExprValue right) {
        super("div", left, right);
    }

    @Override
    public Integer getValue() {
        return left.getValue().intValue() / right.getValue().intValue();
    }

    @Override
    public Type getType() {
        return Type.primitives.get("integer");
    }
}
