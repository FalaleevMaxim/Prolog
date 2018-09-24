package ru.prolog.std;

import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;

import java.util.List;

public class Cut extends AbstractPredicate {
    public Cut() {
        super("!");
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        return 0;
    }
}
