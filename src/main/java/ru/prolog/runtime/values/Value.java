package ru.prolog.runtime.values;

import ru.prolog.model.type.Type;
import ru.prolog.model.values.ValueModel;

import java.util.List;

public interface Value {
    Object getValue();
    Type getType();
    boolean unify(Value other);
    List<Variable> innerFreeVariables();
    ValueModel toModel();
}
