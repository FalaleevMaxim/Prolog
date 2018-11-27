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

public class SumIntPredicate extends AbstractPredicate {
    public SumIntPredicate(TypeStorage typeStorage) {
        super("sumInt", Arrays.asList("integer", "integer", "integer"), typeStorage);
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
            throw new FreeVariableException("More than one free variable in sumInt predicate", (Variable) firstFree);
        }
        Integer left = (Integer) args.get(0).getContent();
        Integer right = (Integer) args.get(1).getContent();
        Integer sum = (Integer) args.get(2).getContent();
        if(left==null){
            return new SimpleValue(typeStorage.get("integer"), sum - right).unify(args.get(0))
                    ? PredicateResult.LAST_RESULT
                    : PredicateResult.FAIL;
        }
        if(right==null){
            return new SimpleValue(typeStorage.get("integer"), sum - left).unify(args.get(1))
                    ? PredicateResult.LAST_RESULT
                    : PredicateResult.FAIL;
        }
        return new SimpleValue(typeStorage.get("integer"), left + right).unify(args.get(2))
                ? PredicateResult.LAST_RESULT
                : PredicateResult.FAIL;
    }
}
