package ru.prolog.std.cast;

import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.simple.SimpleValue;
import ru.prolog.logic.storage.type.TypeStorage;

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
