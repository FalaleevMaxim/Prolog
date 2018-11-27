package ru.prolog.std.cast;

import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.simple.SimpleValue;

public class RealIntCastPredicate extends AbstractCastPredicate {
    public RealIntCastPredicate(TypeStorage typeStorage) {
        super("real_int", "real", "integer", typeStorage);
    }

    @Override
    protected boolean castUnify(Value left, Value right) {
        if(isFreeVariable(left)){
            return new SimpleValue(typeStorage.get("real"), ((Number) right.getContent()).doubleValue()).unify(left);
        }
        return new SimpleValue(typeStorage.get("integer"), ((Number) left.getContent()).intValue()).unify(right);
    }
}
