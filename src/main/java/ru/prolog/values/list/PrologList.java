package ru.prolog.values.list;

import ru.prolog.model.type.Type;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.Value;

public interface PrologList extends Value {
    boolean isEmpty();
    boolean isLast();
    Value head();
    Value getValue();
    PrologList tail();
    PrologList join(Value value);

    static PrologList asList(Type type, Value... values){
        PrologList list = new ListValue(type);
        return asList(new ListValue(type), values);
    }

    static PrologList asList(PrologList tail, Value... values){
        for(int i=values.length-1;i>=0;i--){
            tail = tail.join(values[i]);
        }
        return tail;
    }
}