package ru.prolog.std.string;

import ru.prolog.logic.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;
import ru.prolog.logic.runtime.values.simple.SimpleValue;
import ru.prolog.logic.storage.type.TypeStorage;

import java.util.Arrays;
import java.util.List;

public class FrontCharPredicate extends AbstractPredicate {
    public FrontCharPredicate(TypeStorage typeStorage) {
        super("frontchar", Arrays.asList("string", "char", "string"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;

        if(!isFreeVariable(args.get(0))){
            String str = (String) args.get(0).getValue();
            char front = str.charAt(0);
            String rest = str.substring(1);
            if (!new SimpleValue(typeStorage.get("char"), front).unify(args.get(1))) return -1;
            if (!new SimpleValue(typeStorage.get("string"), rest).unify(args.get(2))) return -1;
            return 0;
        }

        if(isFreeVariable(args.get(1)))
            throw new FreeVariableException("Full string and front char are free variables", (Variable) args.get(1));
        if(isFreeVariable(args.get(2)))
            throw new FreeVariableException("Full string and ret part are free variables", (Variable) args.get(2));
        char front = (char) args.get(1).getValue();
        String rest = (String) args.get(2).getValue();
        String full = front+rest;
        if (!new SimpleValue(typeStorage.get("string"), full).unify(args.get(0))) return -1;
        return 0;
    }
}
