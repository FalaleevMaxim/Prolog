package ru.prolog.runtime.values.expression;

import ru.prolog.model.values.ExprValueModel;
import ru.prolog.runtime.values.Value;

public interface ExprValue extends Value {
    String getName();
    @Override
    Number getContent();
    @Override
    ExprValueModel toModel();

    default boolean hasFreeVariables() {
        return !innerFreeVariables().isEmpty();
    }
}
