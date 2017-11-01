package ru.prolog.model.values;

import ru.prolog.model.Type;
import ru.prolog.model.predicates.execution.rule.RuleExecution;

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
    public Value forContext(RuleExecution context) {
        return this;
    }

    @Override
    public String toString() {
        return "_";
    }
}
