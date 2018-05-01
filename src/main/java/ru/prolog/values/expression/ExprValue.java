package ru.prolog.values.expression;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.Value;
import ru.prolog.values.model.ExprValueModel;

import java.util.List;

public interface ExprValue extends Value {
    String getName();
    @Override
    Number getValue();
    @Override
    ExprValueModel toModel();
}
