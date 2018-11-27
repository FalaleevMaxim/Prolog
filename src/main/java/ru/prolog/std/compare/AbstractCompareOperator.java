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
            throw new FreeVariableException(String.format("Free variable %s in compare operator %s", free.get(0), name), free.get(0));
        if (!args.get(0).getType().isPrimitive())
            throw new PrologRuntimeException(String.format("Illegal argument %s for operator %s. Can compare only primitives.", args.get(0), name));
        if (!args.get(1).getType().isPrimitive())
            throw new PrologRuntimeException(String.format("Illegal argument %s for operator %s. Can compare only primitives.", args.get(0), name));
        if (args.get(0).getType().getPrimitiveType().isString()) {
            if (!args.get(1).getType().getPrimitiveType().isString())
                throw new PrologRuntimeException(String.format("Illegal arguments %s and %s for operator %s. Can not compare string with number.", args.get(0), args.get(1), name));
            return compareStrings((String) args.get(0).getContent(), (String) args.get(1).getContent()) >= 0
                    ? PredicateResult.LAST_RESULT
                    : PredicateResult.FAIL;
        }
        return compareNumbers(((Number) args.get(0).getContent()).doubleValue(), ((Number) args.get(1).getContent()).doubleValue()) >= 0
                ? PredicateResult.LAST_RESULT
                : PredicateResult.FAIL;
    }

    protected abstract int compareStrings(String left, String right);

    protected abstract int compareNumbers(double left, double right);
}
