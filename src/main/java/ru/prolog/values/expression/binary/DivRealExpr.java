package ru.prolog.values.expression.binary;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.type.Type;
import ru.prolog.values.expression.ExprValue;

public class DivRealExpr extends AbstractBinaryExpr {
    public DivRealExpr(ExprValue left, ExprValue right) {
        super("/", left, right);
    }

    @Override
    public Double getValue() {
        return left.getValue().doubleValue() / right.getValue().doubleValue();
    }

    @Override
    public Type getType() {
        return Type.primitives.get("real");
    }
}
