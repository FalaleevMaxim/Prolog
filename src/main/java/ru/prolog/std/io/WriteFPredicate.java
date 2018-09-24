package ru.prolog.std.io;

import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.util.ToStringUtil;
import ru.prolog.util.io.OutputDevice;

import java.util.Arrays;
import java.util.List;

public class WriteFPredicate extends AbstractPredicate{

    public WriteFPredicate(TypeStorage typeStorage) {
        super("writef", Arrays.asList("string", "..."), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith==1) return -1;
        String format = (String)args.get(0).getValue();
        OutputDevice out = context.programContext().getOutputDevices();
        out.print(ToStringUtil.prologFormat(format, args.subList(1,args.size())));
        return 0;
    }
}
