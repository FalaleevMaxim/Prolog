package ru.prolog.model.predicates.predicate.std;

import ru.prolog.model.predicates.execution.predicate.PredicateExecution;
import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.values.Value;

import java.util.Arrays;
import java.util.List;

public class MoreOperatorPredicate extends Predicate {
    public MoreOperatorPredicate(String name, List<String> argTypes) {
        super(">", Arrays.asList("integer", "integer"));
    }

    @Override
    public int run(PredicateExecution context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        return (int)args.get(0).getValue() > (int)args.get(1).getValue() ? 0 : -1;
    }
}
