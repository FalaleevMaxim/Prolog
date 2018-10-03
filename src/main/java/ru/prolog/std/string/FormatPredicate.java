package ru.prolog.std.string;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.runtime.values.simple.SimpleValue;
import ru.prolog.util.ToStringUtil;

import java.util.Arrays;
import java.util.List;

public class FormatPredicate extends AbstractPredicate {
    public FormatPredicate(TypeStorage typeStorage) {
        super("format", Arrays.asList("string", "string", "..."), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        if(isFreeVariable(args.get(0)))
            throw new FreeVariableException("Free variable "+args.get(0)+" as format string in format predicate", (Variable) args.get(0));
        for (int i = 2; i < args.size(); i++) {
            if(isFreeVariable(args.get(i)))
                throw new FreeVariableException("Free variable "+args.get(i)+" as insert value in format predicate", (Variable) args.get(i));
        }
        String format = (String)args.get(0).getValue();
        return new SimpleValue(typeStorage.get("string"), ToStringUtil.prologFormat(format, args.subList(2, args.size()))).unify(args.get(1))
                ? PredicateResult.LAST_RESULT
                : PredicateResult.FAIL;
    }
}
