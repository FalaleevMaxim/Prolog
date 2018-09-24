package ru.prolog.std;

import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;

import java.util.List;

public class Fail extends AbstractPredicate {
    public Fail() {
        super("fail");
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        return -1;
    }
}
