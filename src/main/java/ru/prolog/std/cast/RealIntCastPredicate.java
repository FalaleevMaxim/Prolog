package ru.prolog.std.cast;

import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.simple.SimpleValue;
import ru.prolog.logic.storage.type.TypeStorage;

public class RealIntCastPredicate extends AbstractCastPredicate {
    public RealIntCastPredicate(TypeStorage typeStorage) {
        super("real_int", "real", "integer", typeStorage);
    }

    @Override
    protected boolean castUnify(Value left, Value right) {
        if(isFreeVariable(left)){
            return new SimpleValue(typeStorage.get("real"), ((Number) right.getValue()).doubleValue()).unify(left);
        }
        return new SimpleValue(typeStorage.get("integer"), ((Number) left.getValue()).intValue()).unify(right);
    }
}
