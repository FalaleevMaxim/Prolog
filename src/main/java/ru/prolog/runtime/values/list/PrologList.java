package ru.prolog.runtime.values.list;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.Value;

public interface PrologList extends Value {
    boolean isEmpty();
    boolean isLast();
    Value head();

    default Value getContent() {
        return head();
    }
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