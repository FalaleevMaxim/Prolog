package ru.prolog.logic.std.cast;

import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.simple.SimpleValue;

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
