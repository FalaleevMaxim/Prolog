package ru.prolog.logic.values;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.values.ValueModel;

import java.util.List;

public interface Value {
    Object getValue();
    Type getType();
    boolean unify(Value other);
    List<Variable> innerFreeVariables();
    ValueModel toModel();
}
