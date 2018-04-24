package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.AbstractRule;
import ru.prolog.values.Value;

import java.util.Collections;
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
