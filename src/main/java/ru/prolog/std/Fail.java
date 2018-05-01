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
        super("fail");
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        return -1;
    }
}
