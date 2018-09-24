package ru.prolog.std.io;

import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.util.io.OutputDevice;

import java.util.List;

public class Nl extends AbstractPredicate {
    public Nl() {
        super("nl");
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        OutputDevice out = context.programContext().getOutputDevices();
        out.print("\n");
        return 0;
    }
}
