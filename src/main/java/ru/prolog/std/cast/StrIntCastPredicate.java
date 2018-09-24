package ru.prolog.std.cast;

import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.simple.SimpleValue;
import ru.prolog.logic.storage.type.TypeStorage;

public class StrIntCastPredicate extends AbstractCastPredicate {
    public StrIntCastPredicate(TypeStorage typeStorage) {
        super("str_int", "string", "integer", typeStorage);
    }

    @Override
    protected boolean castUnify(Value left, Value right) {
        if(isFreeVariable(left)){
            return new SimpleValue(typeStorage.get("string"), right.toString()).unify(left);
        }
        String str = (String) left.getValue();
        try{
            return new SimpleValue(typeStorage.get("integer"), Integer.valueOf(str)).unify(right);
        }catch (NumberFormatException nfe){
            return false;
        }
    }
}
