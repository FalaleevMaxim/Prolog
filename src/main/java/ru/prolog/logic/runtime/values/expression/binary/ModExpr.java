package ru.prolog.logic.runtime.values.expression.binary;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.runtime.values.expression.ExprValue;

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
