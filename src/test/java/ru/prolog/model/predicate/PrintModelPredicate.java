package ru.prolog.model.predicate;

import ru.prolog.model.program.Program;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.util.io.OutputDevice;

import java.util.List;

public class PrintModelPredicate extends AbstractPredicate {
    public PrintModelPredicate() {
        super("printModel");
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        Program model = context.programContext().program();
        OutputDevice out = context.programContext().getOutputDevices();
        out.println(model.toString());
        return PredicateResult.LAST_RESULT;
    }
}
