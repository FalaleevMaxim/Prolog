package ru.prolog.runtime.values.expression.binary;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.expression.ExprValue;

public class ModExpr extends AbstractBinaryExpr {
    public ModExpr(ExprValue left, ExprValue right) {
        super("mod", left, right);
    }

    @Override
    public Integer getValue() {
        checkFreeVariables();
        return left.getValue().intValue() % right.getValue().intValue();
    }

    @Override
    public Type getType() {
        return Type.primitives.get("integer");
    }
}
