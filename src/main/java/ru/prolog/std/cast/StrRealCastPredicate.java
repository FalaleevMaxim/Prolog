package ru.prolog.std.cast;

import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.simple.SimpleValue;

public class StrRealCastPredicate extends AbstractCastPredicate {
    public StrRealCastPredicate(TypeStorage typeStorage) {
        super("str_real", "string", "real", typeStorage);
    }

    @Override
    protected boolean castUnify(Value left, Value right) {
        if(isFreeVariable(left)){
            return new SimpleValue(typeStorage.get("string"), right.toString()).unify(left);
        }
        String str = (String) left.getValue();
        try{
            return new SimpleValue(typeStorage.get("real"), Double.valueOf(str)).unify(right);
        }catch (NumberFormatException nfe){
            return false;
        }
    }
}
