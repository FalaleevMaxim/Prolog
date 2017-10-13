package ru.prolog.model.values;

import ru.prolog.model.Type;

public interface Value {
    Object getValue();
    Type getType();
    Boolean unify(Value other);
}
