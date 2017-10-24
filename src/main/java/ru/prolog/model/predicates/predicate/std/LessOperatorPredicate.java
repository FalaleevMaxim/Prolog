package ru.prolog.model.predicates.predicate.std;

import ru.prolog.model.predicates.execution.predicate.PredicateExecution;
import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.values.Value;

import java.util.Arrays;
import java.util.List;

public class LessOperatorPredicate extends Predicate{
    public LessOperatorPredicate() {
        super("<", Arrays.asList("integer", "integer"));
    }

    @Override
    public int run(PredicateExecution context, List<Value> args, int startWith) {
        return 0;
    }
}
