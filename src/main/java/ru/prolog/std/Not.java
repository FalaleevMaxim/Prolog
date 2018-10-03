package ru.prolog.std;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.model.AbstractModelObject;
import ru.prolog.model.ModelObject;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.model.type.Type;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

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
    public PredicateResult run(PredicateContext context, List<Value> args) {
        //Does not allow free variables as args of inner predicate
        for (Value arg : args) {
            for (Variable var : arg.innerFreeVariables()) {
                if (!var.getName().equals("_")) {
                    throw new FreeVariableException("Free variables are not allowed in \"not\"", var);
                }
            }
        }
        return inner.run(context, args).fail()
                ? PredicateResult.LAST_RESULT
                : PredicateResult.FAIL;
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
