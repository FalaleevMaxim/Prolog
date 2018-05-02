package ru.prolog.std;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.exceprions.FreeVariableException;
import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.type.Type;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.AnonymousVariable;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;

import java.util.Collection;
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
        return inner.getName();
    }

    @Override
    public List<String> getArgTypeNames() {
        return inner.getArgTypeNames();
    }

    @Override
    public List<Type> getArgTypes() {
        return inner.getArgTypes();
    }

    @Override
    public int getArity() {
        return inner.getArity();
    }

    @Override
    public TypeStorage getTypeStorage() {
        return inner.getTypeStorage();
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        //Does not work on backtracking
        if(startWith>0) return -1;
        //Does not allow free variables as args of inner predicate
        for(Value arg : args){
            for(Variable var : arg.innerFreeVariables()){
                if(!(var instanceof AnonymousVariable)){
                    throw new FreeVariableException("Free variables are not allowed in \"not\"", var);
                }
            }
        }
        //0 if predicate failed and -1 if not failed
        return inner.run(context, args, startWith)<0?0:-1;
    }

    @Override
    public String toString() {
        return "not(" + inner + ")";
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        return inner.exceptions();
    }

    @Override
    public ModelObject fix() {
        return inner.fix();
    }
}
