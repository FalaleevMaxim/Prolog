package ru.prolog.logic.context.predicate;

import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.util.io.OutputDevice;

public class InteractivePredicateDebugger extends BasePredicateContextDecorator {
    public InteractivePredicateDebugger(PredicateContext decorated) {
        super(decorated);
    }

    @Override
    public boolean execute() {
        OutputDevice out = (OutputDevice) programContext().getContextData(ProgramContext.KEY_DEBUG_OUTPUT_DEVICE);
        out.println("Enter ");
        return false;
    }
}
