package ru.prolog.values;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.type.Type;

public class ExprValue implements Value {
    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public boolean unify(Value other) {
        return null;
    }

    @Override
    public Value forContext(RuleContext context) {
        return null;
    }
}
