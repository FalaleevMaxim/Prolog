package ru.prolog.std.cast;

import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.simple.SimpleValue;

public class StrCharCastPredicate extends AbstractCastPredicate {
    public StrCharCastPredicate(TypeStorage typeStorage) {
        super("str_char", "string", "char", typeStorage);
    }

    @Override
    protected boolean castUnify(Value left, Value right) {
        if(isFreeVariable(left)){
            return new SimpleValue(typeStorage.get("string"), right.getValue().toString()).unify(left);
        }
        String str = (String) left.getValue();
        if(str.length()!=1) return false;
        return new SimpleValue(typeStorage.get("char"), str.charAt(0)).unify(right);
    }
}
