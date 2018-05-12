package ru.prolog.logic.std;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;

import java.util.Arrays;
import java.util.List;

public class EqualsOperatorPredicate extends AbstractPredicate {
    public EqualsOperatorPredicate(TypeStorage typeStorage) {
        super("=", Arrays.asList("_", "_"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        return args.get(0).unify(args.get(1)) ? 0 : -1;
    }
}
