package ru.prolog.logic.runtime.values.list;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.runtime.values.Value;

public interface PrologList extends Value {
    boolean isEmpty();
    boolean isLast();
    Value head();
    default Value getValue(){return head();}
    PrologList tail();
    PrologList join(Value value);

    static PrologList asList(Type type, Value... values){
        return asList(new ListValue(type), values);
    }

    static PrologList asList(PrologList tail, Value... values){
        for(int i=values.length-1;i>=0;i--){
            tail = tail.join(values[i]);
        }
        return tail;
    }
}