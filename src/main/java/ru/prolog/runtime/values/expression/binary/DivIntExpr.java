package ru.prolog.runtime.values.expression.binary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class DivIntExpr extends AbstractBinaryExpr{
    public DivIntExpr(ExprValue left, ExprValue right) {
        super("div", left, right);
    }

    @Override
    public Integer getContent() {
        checkFreeVariables();
        return left.getContent().intValue() / right.getContent().intValue();
    }

    @Override
    public Type getType() {
        return Type.primitives.get("integer");
    }
}
