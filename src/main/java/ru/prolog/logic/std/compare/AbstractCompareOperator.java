package ru.prolog.logic.std.compare;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.exceptions.FreeVariableException;
import ru.prolog.logic.exceptions.PrologRuntimeException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractCompareOperator extends AbstractPredicate {
    public AbstractCompareOperator(String name, TypeStorage typeStorage) {
        super(name, Arrays.asList("_", "_"), typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(startWith>0) return -1;
        List<Variable> free = args.get(0).innerFreeVariables();
        if(free.isEmpty()) free = args.get(1).innerFreeVariables();
        if(!free.isEmpty())
            throw new FreeVariableException("Free variable "+free.get(0)+" in compare operator "+name, free.get(0));
        if(!args.get(0).getType().isPrimitive())
            throw new PrologRuntimeException("Illegal argument "+ args.get(0) +" for operator "+name+". Can compare only primitives.");
        if(!args.get(1).getType().isPrimitive())
            throw new PrologRuntimeException("Illegal argument "+ args.get(0) +" for operator "+name+". Can compare only primitives.");
        if(args.get(0).getType().getPrimitiveType().isString()){
            if(!args.get(1).getType().getPrimitiveType().isString())
                throw new PrologRuntimeException("Illegal arguments "+ args.get(0) + " and "+args.get(1)+" for operator "+name+". Can not compare string with number.");
            return compareStrings((String)args.get(0).getValue(), (String)args.get(1).getValue());
        }
        return compareNumbers(((Number)args.get(0).getValue()).doubleValue(), ((Number)args.get(1).getValue()).doubleValue());
    }

    protected abstract int compareStrings(String left, String right);
    protected abstract int compareNumbers(double left, double right);
}
