package ru.prolog.values;

import ru.prolog.context.RuntimeObject;
import ru.prolog.model.type.Type;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.model.ValueModel;

import java.util.List;

public interface Value extends RuntimeObject{
    Object getValue();
    Type getType();
    boolean unify(Value other);
    List<Variable> innerFreeVariables();
    ValueModel toModel();
}
