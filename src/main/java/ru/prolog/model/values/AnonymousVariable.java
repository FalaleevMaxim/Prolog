package ru.prolog.model.values;

import ru.prolog.model.Type;

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
}
