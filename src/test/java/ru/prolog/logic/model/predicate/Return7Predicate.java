package ru.prolog.logic.model.predicate;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.simple.SimpleValue;

import java.util.Collections;
import java.util.List;

public class Return7Predicate extends AbstractPredicate {
    public Return7Predicate(TypeStorage typeStorage) {
        super("return7", Collections.singletonList("integer"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        return new SimpleValue(typeStorage.get("integer"), 7).unify(args.get(0)) ? 0 : -1;
    }
}