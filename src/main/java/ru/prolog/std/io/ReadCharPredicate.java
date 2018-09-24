package ru.prolog.std.io;

import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.simple.SimpleValue;
import ru.prolog.logic.storage.type.TypeStorage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ReadCharPredicate extends AbstractPredicate {

    public ReadCharPredicate(TypeStorage typeStorage) {
        super("readchar", Collections.singletonList("char"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        try {
            char c = context.programContext().getInputDevice().readChar();
            if(c==27) return -1; //Escape pressed
            return new SimpleValue(typeStorage.get("char"), c).unify(args.get(0)) ? 0 : -1;
        } catch (IOException e) {
            context.programContext().getErrorListeners().println("Can not read char");
            return -1;
        }
    }
}
