package ru.prolog.model.values;

import ru.prolog.model.Type;
import ru.prolog.model.predicates.execution.rule.RuleExecution;

public interface Value {
    Object getValue();
    Type getType();
    Boolean unify(Value other);
    Value forContext(RuleExecution context);
}
