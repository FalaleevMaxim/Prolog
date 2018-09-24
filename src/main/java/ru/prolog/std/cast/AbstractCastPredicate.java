package ru.prolog.std.cast;

import ru.prolog.logic.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;
import ru.prolog.logic.storage.type.TypeStorage;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractCastPredicate extends AbstractPredicate {

    public AbstractCastPredicate(String name, String t1, String t2, TypeStorage typeStorage) {
        super(name, Arrays.asList(t1,t2), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        Value left = args.get(0);
        Value right = args.get(1);
        if(isFreeVariable(left) && isFreeVariable(right)){
            throw new FreeVariableException("Both arguments of cast predicate " + name +" are free variables", (Variable) left);
        }
        return castUnify(left, right)?0:-1;
    }

    protected abstract boolean castUnify(Value left, Value right);
}
