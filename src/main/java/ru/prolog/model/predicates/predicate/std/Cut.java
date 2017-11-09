package ru.prolog.model.predicates.predicate.std;

import ru.prolog.model.predicates.execution.predicate.PredicateExecution;
import ru.prolog.model.predicates.predicate.Predicate;
import ru.prolog.model.values.Value;

import java.util.Collections;
import java.util.List;

public class Cut extends Predicate {
    public Cut() {
        super("!", Collections.emptyList());
    }

    @Override
    public int run(PredicateExecution context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        return 0;
    }
}
