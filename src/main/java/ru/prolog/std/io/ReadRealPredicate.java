package ru.prolog.std.io;

import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.simple.SimpleValue;
import ru.prolog.logic.storage.type.TypeStorage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ReadRealPredicate extends AbstractPredicate {

    public ReadRealPredicate(TypeStorage typeStorage) {
        super("readreal", Collections.singletonList("real"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        try {
            String str = context.programContext().getInputDevice().readLine();
            if(str==null) return -1;
            return new SimpleValue(typeStorage.get("real"), Double.parseDouble(str)).unify(args.get(0)) ? 0 : -1;
        } catch (IOException e) {
            context.programContext().getErrorListeners().println("Read error");
            return -1;
        } catch (NumberFormatException e){
            return -1;
        }
    }
}
