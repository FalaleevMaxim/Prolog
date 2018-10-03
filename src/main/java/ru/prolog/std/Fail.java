package ru.prolog.std;

import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;

import java.util.List;

public class Fail extends AbstractPredicate {
    public Fail() {
        super("fail");
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        return PredicateResult.FAIL;
    }
}
