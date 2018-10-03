package ru.prolog.logic.runtime.values.expression;

import ru.prolog.logic.model.values.ExprValueModel;
import ru.prolog.logic.runtime.values.Value;

public interface ExprValue extends Value {
    String getName();
    @Override
    Number getValue();
    @Override
    ExprValueModel toModel();

    default boolean hasFreeVariables() {
        return innerFreeVariables().size() > 0;
    }
}
