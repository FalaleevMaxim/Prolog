package ru.prolog.logic.std.io;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.values.Value;
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