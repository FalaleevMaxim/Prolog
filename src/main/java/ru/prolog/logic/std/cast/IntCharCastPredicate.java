package ru.prolog.logic.std.cast;

import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.simple.SimpleValue;

public class IntCharCastPredicate extends AbstractCastPredicate {
    public IntCharCastPredicate(TypeStorage typeStorage) {
        super("int_char", "integer", "char", typeStorage);
    }

    @Override
    protected boolean castUnify(Value left, Value right) {
        if(isFreeVariable(left)){
            char value = (Character)right.getValue();
            Integer intVal = (int)value;
            return new SimpleValue(typeStorage.get("integer"), intVal).unify(left);
        }
        int value = (Integer)left.getValue();
        if(value<0 || value>Character.MAX_VALUE) return false;
        Character c = (char) value;
        return new SimpleValue(typeStorage.get("char"), c).unify(right);
    }
}