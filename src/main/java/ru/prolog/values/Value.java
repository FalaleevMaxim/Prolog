package ru.prolog.values;

import ru.prolog.model.type.Type;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.variables.Variable;

import java.util.List;

public interface Value {
    Object getValue();
    Type getType();
    boolean unify(Value other);
    Value forContext(RuleContext context);
    List<Variable> innerVariables();
}
