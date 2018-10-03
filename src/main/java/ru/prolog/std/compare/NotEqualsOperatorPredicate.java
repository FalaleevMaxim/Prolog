package ru.prolog.std.compare;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

import java.util.Arrays;
import java.util.List;

public class NotEqualsOperatorPredicate extends AbstractPredicate {
    public NotEqualsOperatorPredicate(TypeStorage typeStorage) {
        super("<>", Arrays.asList("_","_"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        Value left = args.get(0);
        Value right = args.get(1);
        if(isFreeVariable(left))
            throw new FreeVariableException("Free variable "+left+" in <> operation.", (Variable) left);
        if(isFreeVariable(right))
            throw new FreeVariableException("Free variable "+right+" in <> operation.", (Variable) right);
        return left.unify(right) ? PredicateResult.FAIL : PredicateResult.LAST_RESULT;
    }
}
