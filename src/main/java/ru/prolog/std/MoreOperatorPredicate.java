package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.Value;

import java.util.Arrays;
import java.util.List;

public class MoreOperatorPredicate extends AbstractPredicate {
    public MoreOperatorPredicate(TypeStorage typeStorage) {
        super(">", Arrays.asList("integer", "integer"),typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        return (int)args.get(0).getValue() > (int)args.get(1).getValue() ? 0 : -1;
    }
}
