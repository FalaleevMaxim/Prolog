package ru.prolog.values.expression.binary;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.type.Type;
import ru.prolog.values.expression.ExprValue;

public class ModExpr extends AbstractBinaryExpr {
    public ModExpr(ExprValue left, ExprValue right) {
        super("mod", left, right);
    }

    @Override
    public Integer getValue() {
        return left.getValue().intValue() % right.getValue().intValue();
    }

    @Override
    public Type getType() {
        return Type.primitives.get("integer");
    }
}
