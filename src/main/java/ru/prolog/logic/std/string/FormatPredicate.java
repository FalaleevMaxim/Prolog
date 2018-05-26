package ru.prolog.logic.std.string;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.exceptions.FreeVariableException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
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
        if(isFreeVariable(args.get(0)))
            throw new FreeVariableException("Free variable "+args.get(0)+" as format string in format predicate", (Variable) args.get(0));
        for (int i = 2; i < args.size(); i++) {
            if(isFreeVariable(args.get(i)))
                throw new FreeVariableException("Free variable "+args.get(i)+" as insert value in format predicate", (Variable) args.get(i));
        }
        String format = (String)args.get(0).getValue();
        return new SimpleValue(typeStorage.get("string"), ToStringUtil.prologFormat(format, args.subList(2,args.size()))).unify(args.get(1))?0:-1;
    }
}
