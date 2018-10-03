package ru.prolog.std.compare;

import ru.prolog.etc.exceptions.runtime.FreeVariableException;
import ru.prolog.etc.exceptions.runtime.PrologRuntimeException;
import ru.prolog.model.predicate.AbstractPredicate;
import ru.prolog.model.predicate.PredicateResult;
import ru.prolog.model.storage.type.TypeStorage;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractCompareOperator extends AbstractPredicate {
    public AbstractCompareOperator(String name, TypeStorage typeStorage) {
        super(name, Arrays.asList("_", "_"), typeStorage);
    }

    @Override
    public PredicateResult run(PredicateContext context, List<Value> args) {
        List<Variable> free = args.get(0).innerFreeVariables();
        if (free.isEmpty()) free = args.get(1).innerFreeVariables();
        if (!free.isEmpty())
            throw new FreeVariableException("Free variable " + free.get(0) + " in compare operator " + name, free.get(0));
        if (!args.get(0).getType().isPrimitive())
            throw new PrologRuntimeException("Illegal argument " + args.get(0) + " for operator " + name + ". Can compare only primitives.");
        if (!args.get(1).getType().isPrimitive())
            throw new PrologRuntimeException("Illegal argument " + args.get(0) + " for operator " + name + ". Can compare only primitives.");
        if (args.get(0).getType().getPrimitiveType().isString()) {
            if (!args.get(1).getType().getPrimitiveType().isString())
                throw new PrologRuntimeException("Illegal arguments " + args.get(0) + " and " + args.get(1) + " for operator " + name + ". Can not compare string with number.");
            return compareStrings((String) args.get(0).getValue(), (String) args.get(1).getValue()) >= 0
                    ? PredicateResult.LAST_RESULT
                    : PredicateResult.FAIL;
        }
        return compareNumbers(((Number) args.get(0).getValue()).doubleValue(), ((Number) args.get(1).getValue()).doubleValue()) >= 0
                ? PredicateResult.LAST_RESULT
                : PredicateResult.FAIL;
    }

    protected abstract int compareStrings(String left, String right);

    protected abstract int compareNumbers(double left, double right);
}
