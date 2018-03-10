package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.predicates.rule.Statement;
import ru.prolog.values.Value;

import java.util.Collections;
import java.util.List;

public class Nl extends Predicate {
    public Nl() {
        super("nl", Collections.emptyList());
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        System.out.println();
        return 0;
    }

    private static Predicate instance;
    static Predicate instance(){
        if(instance==null) instance = new Nl();
        return instance;
    }
    public static Statement statement(){
        return new Statement(instance(), Collections.emptyList());
    }
}
