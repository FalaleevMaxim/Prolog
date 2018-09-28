package ru.prolog.std;

import ru.prolog.logic.etc.exceptions.model.ModelStateException;
import ru.prolog.logic.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.logic.model.AbstractModelObject;
import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;
import ru.prolog.logic.storage.type.TypeStorage;

import java.util.Collection;
import java.util.List;

/**
 * Wraps other predicate? and fails if it succeeds? succeeds if predicate failed.
 * Does not allow free variables as atgs of inner predicate
 */
public class Not extends AbstractModelObject implements Predicate {
    private Predicate inner;

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
                if(!var.getName().equals("_")){
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