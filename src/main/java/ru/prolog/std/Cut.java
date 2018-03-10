package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.values.Value;

import java.util.Collections;
import java.util.List;

public class Cut extends Predicate {
    public Cut() {
        super("!", Collections.emptyList());
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        return 0;
    }
}
