package ru.prolog.std.cast;

import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.simple.SimpleValue;

public class StringSymbolCastPredicate extends AbstractCastPredicate {
    public StringSymbolCastPredicate(TypeStorage typeStorage) {
        super("string_symbol", "string", "symbol", typeStorage);
    }

    @Override
    protected boolean castUnify(Value left, Value right) {
        if(isFreeVariable(left)){
            return new SimpleValue(typeStorage.get("string"), right.getValue()).unify(left);
        }
        return new SimpleValue(typeStorage.get("symbol"), left.getValue()).unify(right);
    }
}
