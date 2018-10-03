package ru.prolog.std;

import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;

import java.util.List;

public class Cut extends AbstractPredicate {
    public Cut() {
        super("!");
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        return PredicateResult.LAST_RESULT;
    }
}
