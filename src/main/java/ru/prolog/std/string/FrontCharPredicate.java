package ru.prolog.std.string;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.runtime.values.simple.SimpleValue;

import java.util.Arrays;
import java.util.List;

public class FrontCharPredicate extends AbstractPredicate {
    public FrontCharPredicate(TypeStorage typeStorage) {
        super("frontchar", Arrays.asList("string", "char", "string"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        if(!isFreeVariable(args.get(0))){
            String str = (String) args.get(0).getContent();
            char front = str.charAt(0);
            String rest = str.substring(1);
            if (!new SimpleValue(typeStorage.get("char"), front).unify(args.get(1))) return PredicateResult.FAIL;
            if (!new SimpleValue(typeStorage.get("string"), rest).unify(args.get(2))) return PredicateResult.FAIL;
            return PredicateResult.LAST_RESULT;
        }

        if(isFreeVariable(args.get(1)))
            throw new FreeVariableException("Full string and front char are free variables", (Variable) args.get(1));
        if(isFreeVariable(args.get(2)))
            throw new FreeVariableException("Full string and ret part are free variables", (Variable) args.get(2));
        char front = (char) args.get(1).getContent();
        String rest = (String) args.get(2).getContent();
        String full = front+rest;
        if (!new SimpleValue(typeStorage.get("string"), full).unify(args.get(0))) return PredicateResult.FAIL;
        return PredicateResult.LAST_RESULT;
    }
}
