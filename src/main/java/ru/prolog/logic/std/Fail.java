package ru.prolog.logic.std;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.values.Value;

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
