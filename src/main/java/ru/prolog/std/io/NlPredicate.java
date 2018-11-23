package ru.prolog.std.io;

import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.util.io.OutputDevice;

import java.util.List;

public class NlPredicate extends AbstractPredicate {
    public NlPredicate() {
        super("nl");
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        OutputDevice out = context.programContext().getOutputDevices();
        out.print("\n");
        return PredicateResult.LAST_RESULT;
    }
}
