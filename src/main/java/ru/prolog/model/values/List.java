package ru.prolog.model.values;

import ru.prolog.model.Type;

public interface List extends Value {
    boolean isEmpty();
    boolean isLast();
    Value head();
    Value getValue();
    List tail();
    List join(Value value);

    static List asList(Type type, Value... values){
        List list = new ListValue(type);
        return asList(new ListValue(type), values);
    }

    static List asList(List tail, Value... values){
        for(int i=values.length-1;i>=0;i--){
            tail = tail.join(values[i]);
        }
        return tail;
    }
}