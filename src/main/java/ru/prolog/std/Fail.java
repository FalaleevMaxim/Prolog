package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.Statement;
import ru.prolog.values.Value;

import java.util.Collections;
import java.util.List;

public class Fail extends AbstractPredicate {
    public Fail() {
        super("fail", Collections.emptyList());
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        return -1;
    }

    private static Predicate instance;
    static Predicate instance(){
        if(instance==null) instance = new Fail();
        return instance;
    }
    public static Statement statement(){
        return new Statement(instance(), Collections.emptyList());
    }
}
