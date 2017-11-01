package ru.prolog.model.predicates.predicate.std;

import ru.prolog.model.predicates.execution.predicate.PredicateExecution;
import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.values.Value;

import java.util.Collections;
import java.util.List;

public class Fail extends Predicate {
    public Fail() {
        super("fail", Collections.emptyList());
    }

    @Override
    public int run(PredicateExecution context, List<Value> args, int startWith) {
        return -1;
    }
}
