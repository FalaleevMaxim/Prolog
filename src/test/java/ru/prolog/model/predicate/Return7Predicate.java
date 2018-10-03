package ru.prolog.model.predicate;

import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.simple.SimpleValue;

import java.util.Collections;
import java.util.List;

public class Return7Predicate extends AbstractPredicate {
    public Return7Predicate(TypeStorage typeStorage) {
        super("return7", Collections.singletonList("integer"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        return new SimpleValue(typeStorage.get("integer"), 7).unify(args.get(0))
                ? PredicateResult.LAST_RESULT
                : PredicateResult.FAIL;
    }
}
