package ru.prolog.logic.model.predicate;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.values.Value;

import java.util.List;

public class HelloPredicate extends AbstractPredicate {
    public HelloPredicate() {
        super("helloWorld");
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if (startWith>0) return -1;
        context.programContext().getOutputDevices().print("Hello world");
        return 0;
    }
}
