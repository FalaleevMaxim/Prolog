package ru.prolog.logic.std.string;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.simple.SimpleValue;
import ru.prolog.util.ToStringUtil;

import java.util.Arrays;
import java.util.List;

public class FormatPredicate extends AbstractPredicate {
    public FormatPredicate(TypeStorage typeStorage) {
        super("format", Arrays.asList("string", "string", "..."), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith==1) return -1;
        String format = (String)args.get(0).getValue();
        new SimpleValue(typeStorage.get("string"), ToStringUtil.prologFormat(format, args.subList(1,args.size()))).unify(args.get(0));
        return 0;
    }
}
