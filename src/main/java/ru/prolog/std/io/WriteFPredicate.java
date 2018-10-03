package ru.prolog.std.io;

import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.util.ToStringUtil;
import ru.prolog.util.io.OutputDevice;

import java.util.Arrays;
import java.util.List;

public class WriteFPredicate extends AbstractPredicate{

    public WriteFPredicate(TypeStorage typeStorage) {
        super("writef", Arrays.asList("string", "..."), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        String format = (String)args.get(0).getValue();
        OutputDevice out = context.programContext().getOutputDevices();
        out.print(ToStringUtil.prologFormat(format, args.subList(1,args.size())));
        return PredicateResult.LAST_RESULT;
    }
}
