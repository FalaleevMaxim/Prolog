package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.values.Value;

import java.util.List;

public class Cut2 extends AbstractPredicate{
    public Cut2() {
        super("cut");
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        return 0;
    }
}
