package ru.prolog.logic.std.io;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.simple.SimpleValue;
import ru.prolog.util.io.ErrorListener;
import ru.prolog.util.io.InputDevice;

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
            return new SimpleValue(typeStorage.get("char"), c).unify(args.get(0)) ? 0 : -1;
        } catch (IOException e) {
            context.programContext().getErrorListeners().println("Can not read char");
            return -1;
        }
    }
}
