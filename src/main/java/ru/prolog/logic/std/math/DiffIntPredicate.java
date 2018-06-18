package ru.prolog.logic.std.math;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.exceptions.FreeVariableException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
import ru.prolog.logic.values.simple.SimpleValue;

import java.util.Arrays;
import java.util.List;

public class DiffIntPredicate extends AbstractPredicate {
    public DiffIntPredicate(TypeStorage typeStorage) {
        super("diffInt", Arrays.asList("integer", "integer", "integer"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
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
            return new SimpleValue(typeStorage.get("integer"), diff+right).unify(args.get(0))?0:-1;
        }
        if(right==null){
            return new SimpleValue(typeStorage.get("integer"), left-diff).unify(args.get(1))?0:-1;
        }
        return new SimpleValue(typeStorage.get("integer"), left-right).unify(args.get(2))?0:-1;
    }
}
