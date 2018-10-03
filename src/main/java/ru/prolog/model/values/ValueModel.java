package ru.prolog.model.values;

import ru.prolog.model.ModelObject;
import ru.prolog.model.type.Type;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;

import java.util.Set;

public interface ValueModel extends ModelObject {
    Type getType();
    Value forContext(RuleContext context);
    Set<VariableModel> innerVariables();
    void setType(Type type);
}