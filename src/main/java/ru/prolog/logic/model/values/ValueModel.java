package ru.prolog.logic.model.values;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.runtime.context.rule.RuleContext;
import ru.prolog.logic.runtime.values.Value;

import java.util.Set;

public interface ValueModel extends ModelObject {
    Type getType();
    Value forContext(RuleContext context);
    Set<VariableModel> innerVariables();
    void setType(Type type);
}