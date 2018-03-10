package ru.prolog.values;

import ru.prolog.model.Type;
import ru.prolog.context.rule.RuleContext;

public class AnonymousVariable implements Value {
    private Type type;

    public AnonymousVariable(Type type) {
        this.type = type;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Boolean unify(Value other) {
        return true;
    }

    @Override
    public Value forContext(RuleContext context) {
        return this;
    }

    @Override
    public String toString() {
        return "_";
    }
}
