package ru.prolog.model.values;

import ru.prolog.model.Type;
import ru.prolog.model.predicates.execution.rule.RuleExecution;

public interface PrologList extends Value {
    boolean isEmpty();
    boolean isLast();
    Value head();
    Value getValue();
    PrologList tail();
    PrologList join(Value value);
    PrologList forContext(RuleExecution context);

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