package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.values.Value;

import java.util.Arrays;
import java.util.List;

public class EqualsOperatorPredicate extends Predicate{
    public EqualsOperatorPredicate() {
        super("=", Arrays.asList("integer", "integer"));
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        return args.get(0).unify(args.get(1)) ? 0 : -1;
    }
}
