package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.exceprions.FreeVariableException;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.Value;
import ru.prolog.values.variables.Variable;

import java.util.List;

/**
 * Wraps other predicate? and fails if it succeeds? succeeds if predicate failed.
 * Does not allow free variables as atgs of inner predicate
 */
public class Not implements Predicate {
    public Predicate inner;

    public Not(Predicate inner) {
        this.inner = inner;
    }

    @Override
    public String getName() {
        return "not";
    }

    @Override
    public List<String> getArgTypes() {
        return inner.getArgTypes();
    }

    @Override
    public TypeStorage getTypeStorage() {
        return inner.getTypeStorage();
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        //Does not work on backtracking
        if(startWith>0) return -1;
        //Does not allow free variables as atgs of inner predicate
        for(Value arg : args){
            if (arg instanceof Variable && ((Variable)arg).isFree())
                throw new FreeVariableException((Variable)arg);
        }
        //0 if predicate failed and -1 if not failed
        return inner.run(context, args, startWith)<0?0:-1;
    }

    @Override
    public String toString() {
        return "not(" + inner + ")";
    }
}
