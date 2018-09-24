package ru.prolog.std.compare;

import ru.prolog.logic.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;
import ru.prolog.logic.storage.type.TypeStorage;

import java.util.Arrays;
import java.util.List;

public class NotEqualsOperatorPredicate extends AbstractPredicate {
    public NotEqualsOperatorPredicate(TypeStorage typeStorage) {
        super("<>", Arrays.asList("_","_"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        Value left = args.get(0);
        Value right = args.get(1);
        if(isFreeVariable(left))
            throw new FreeVariableException("Free variable "+left+" in <> operation.", (Variable) left);
        if(isFreeVariable(right))
            throw new FreeVariableException("Free variable "+right+" in <> operation.", (Variable) right);
        return left.unify(right)?-1:0;
    }
}
