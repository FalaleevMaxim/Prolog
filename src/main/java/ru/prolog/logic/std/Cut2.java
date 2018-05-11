package ru.prolog.logic.std;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.values.Value;

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
