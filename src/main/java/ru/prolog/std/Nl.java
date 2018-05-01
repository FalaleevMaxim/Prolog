package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.Statement;
import ru.prolog.values.Value;

import java.util.Collections;
import java.util.List;

public class Nl extends AbstractPredicate {
    public Nl() {
        super("nl");
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        System.out.println();
        return 0;
    }
}
