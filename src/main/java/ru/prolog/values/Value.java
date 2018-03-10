package ru.prolog.values;

import ru.prolog.model.Type;
import ru.prolog.context.rule.RuleContext;

public interface Value {
    Object getValue();
    Type getType();
    Boolean unify(Value other);
    Value forContext(RuleContext context);
}
