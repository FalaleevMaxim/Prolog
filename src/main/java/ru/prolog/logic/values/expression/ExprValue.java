package ru.prolog.logic.values.expression;

import ru.prolog.logic.values.Value;
import ru.prolog.logic.model.values.ExprValueModel;

public interface ExprValue extends Value {
    String getName();
    @Override
    Number getValue();
    @Override
    ExprValueModel toModel();
}
