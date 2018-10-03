package ru.prolog.std.math;

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

public class DiffIntPredicate extends AbstractPredicate {
    public DiffIntPredicate(TypeStorage typeStorage) {
        super("diffInt", Arrays.asList("integer", "integer", "integer"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        int freeCount = 0;
        Value firstFree = null;
        for (Value arg : args){
            if(isFreeVariable(arg)){
                freeCount++;
                if(firstFree==null) firstFree = arg;
            }
        }
        if(freeCount>1){
            throw new FreeVariableException("More than one free variable in diffInt predicate", (Variable) firstFree);
        }
        Integer left = (Integer)args.get(0).getValue();
        Integer right = (Integer)args.get(1).getValue();
        Integer diff = (Integer)args.get(2).getValue();
        if(left==null){
            return new SimpleValue(typeStorage.get("integer"), diff + right).unify(args.get(0))
                    ? PredicateResult.LAST_RESULT
                    : PredicateResult.FAIL;
        }
        if(right==null){
            return new SimpleValue(typeStorage.get("integer"), left - diff).unify(args.get(1))
                    ? PredicateResult.LAST_RESULT
                    : PredicateResult.FAIL;
        }
        return new SimpleValue(typeStorage.get("integer"), left - right).unify(args.get(2))
                ? PredicateResult.LAST_RESULT
                : PredicateResult.FAIL;
    }
}
