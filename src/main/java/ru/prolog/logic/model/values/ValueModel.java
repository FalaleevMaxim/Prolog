package ru.prolog.logic.model.values;

import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.Value;

import java.util.List;

public interface ValueModel extends ModelObject {
    Type getType();
    Value forContext(RuleContext context);
    List<VariableModel> innerModelVariables();
    void setType(Type type);
}