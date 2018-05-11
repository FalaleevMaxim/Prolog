package ru.prolog.logic.values;

import ru.prolog.logic.context.RuntimeObject;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.model.ValueModel;

import java.util.List;

public interface Value extends RuntimeObject{
    Object getValue();
    Type getType();
    boolean unify(Value other);
    List<Variable> innerFreeVariables();
    ValueModel toModel();
}
