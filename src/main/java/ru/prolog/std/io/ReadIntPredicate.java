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

public class ReadIntPredicate extends AbstractPredicate {

    public ReadIntPredicate(TypeStorage typeStorage) {
        super("readint", Collections.singletonList("integer"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        try {
            String str = context.programContext().getInputDevice().readLine();
            if (str == null) return PredicateResult.FAIL;
            return new SimpleValue(typeStorage.get("integer"), Integer.parseInt(str)).unify(args.get(0))
                    ? PredicateResult.LAST_RESULT
                    : PredicateResult.FAIL;
        } catch (IOException e) {
            context.programContext().getErrorListeners().println("Read error");
            return PredicateResult.FAIL;
        } catch (NumberFormatException e){
            return PredicateResult.FAIL;
        }
    }
}
