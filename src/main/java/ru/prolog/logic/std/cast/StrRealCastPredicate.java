package ru.prolog.logic.std.cast;

import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.simple.SimpleValue;

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
