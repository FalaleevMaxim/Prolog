package ru.prolog.values.model;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.ModelObject;
import ru.prolog.model.type.Type;
import ru.prolog.values.Value;

import java.util.List;

public interface ValueModel extends ModelObject {
    Type getType();
    Value forContext(RuleContext context);
    List<VariableModel> innerModelVariables();
}