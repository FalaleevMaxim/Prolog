package ru.prolog.std.cast;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractCastPredicate extends AbstractPredicate {

    public AbstractCastPredicate(String name, String t1, String t2, TypeStorage typeStorage) {
        super(name, Arrays.asList(t1,t2), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        Value left = args.get(0);
        Value right = args.get(1);
        if(isFreeVariable(left) && isFreeVariable(right)){
            throw new FreeVariableException("Both arguments of cast predicate " + name +" are free variables", (Variable) left);
        }
        return castUnify(left, right)
                ? PredicateResult.LAST_RESULT
                : PredicateResult.FAIL;
    }

    protected abstract boolean castUnify(Value left, Value right);
}
