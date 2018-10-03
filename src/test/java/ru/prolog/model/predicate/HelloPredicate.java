package ru.prolog.model.predicate;

import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;

import java.util.List;

public class HelloPredicate extends AbstractPredicate {
    public HelloPredicate() {
        super("helloWorld");
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        context.programContext().getOutputDevices().print("Hello world");
        return PredicateResult.LAST_RESULT;
    }
}
