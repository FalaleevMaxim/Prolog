package ru.prolog.std.window;

import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.util.window.PrologWindowManager;

import java.util.Arrays;
import java.util.List;

public class MakeWindowPredicate extends AbstractPredicate {
    public MakeWindowPredicate(TypeStorage typeStorage) {
        super("makeWindow",
                Arrays.asList("integer", "integer", "integer", "string", "integer", "integer", "integer", "integer"),
                typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        checkFreeVariables(args);
        PrologWindowManager windowManager = context.programContext().getWindowManager();
        if (windowManager == null) {
            context.programContext().getErrorListeners().println("Window system not supported!");
            return PredicateResult.FAIL;
        }
        checkFreeVariables(args);
        windowManager.makeWindow(
                (Integer) args.get(0).getContent(),
                (Integer) args.get(0).getContent(),
                (Integer) args.get(0).getContent(),
                (String) args.get(0).getContent(),
                (Integer) args.get(0).getContent(),
                (Integer) args.get(0).getContent(),
                (Integer) args.get(0).getContent(),
                (Integer) args.get(0).getContent());
        return PredicateResult.LAST_RESULT;
    }
}
