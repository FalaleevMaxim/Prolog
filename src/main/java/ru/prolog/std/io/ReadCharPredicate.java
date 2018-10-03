package ru.prolog.std.io;

import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.simple.SimpleValue;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ReadCharPredicate extends AbstractPredicate {

    public ReadCharPredicate(TypeStorage typeStorage) {
        super("readchar", Collections.singletonList("char"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        try {
            char c = context.programContext().getInputDevice().readChar();
            if (c == 27) return PredicateResult.FAIL; //Escape pressed
            return new SimpleValue(typeStorage.get("char"), c).unify(args.get(0))
                    ? PredicateResult.LAST_RESULT
                    : PredicateResult.FAIL;
        } catch (IOException e) {
            context.programContext().getErrorListeners().println("Can not read char");
            return PredicateResult.FAIL;
        }
    }
}
