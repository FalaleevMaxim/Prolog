package ru.prolog.logic.model.predicate;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.values.Value;
import ru.prolog.util.io.OutputDevice;

import java.util.List;

public class PrintModelPredicate extends AbstractPredicate {
    public PrintModelPredicate() {
        super("printModel");
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        Program model = context.programContext().program();
        OutputDevice out = context.programContext().getOutputDevices();
        out.println(model.toString());
        return 0;
    }
}
