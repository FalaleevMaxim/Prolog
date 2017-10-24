package ru.prolog.model.predicates.predicate.std;

import ru.prolog.model.predicates.execution.predicate.PredicateExecution;
import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.values.Value;

import java.util.Collections;
import java.util.List;

public class WritePredicate extends Predicate{
    public WritePredicate() {
        super("write", Collections.singletonList("string"));
    }

    @Override
    public int run(PredicateExecution context, List<Value> args, int startWith) {
        System.out.println(args.get(0).getValue());
        return 0;
    }
}
