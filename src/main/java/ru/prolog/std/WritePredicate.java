package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.predicates.rule.Statement;
import ru.prolog.values.Value;

import java.util.Collections;
import java.util.List;

public class WritePredicate extends Predicate{
    public WritePredicate() {
        super("write", Collections.singletonList("string"));
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        System.out.print(args.get(0));
        return 0;
    }

    private static Predicate instance;
    static Predicate instance(){
        if(instance==null) instance = new WritePredicate();
        return instance;
    }
    public static Statement statement(Value message){
        return new Statement(instance(), Collections.singletonList(message));
    }
}
