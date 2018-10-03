package ru.prolog.std.compare;

import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;

import java.util.Arrays;
import java.util.List;

public class EqualsOperatorPredicate extends AbstractPredicate {
    public EqualsOperatorPredicate(TypeStorage typeStorage) {
        super("=", Arrays.asList("_", "_"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        return args.get(0).unify(args.get(1)) ? PredicateResult.LAST_RESULT : PredicateResult.FAIL;
    }
}
