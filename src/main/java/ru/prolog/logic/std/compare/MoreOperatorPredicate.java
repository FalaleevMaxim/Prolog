package ru.prolog.logic.std.compare;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.exceprions.FreeVariableException;
import ru.prolog.logic.exceprions.PrologRuntimeException;
import ru.prolog.logic.model.predicate.AbstractPredicate;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;

import java.util.Arrays;
import java.util.List;

public class MoreOperatorPredicate extends AbstractCompareOperator {
    public MoreOperatorPredicate(TypeStorage typeStorage) {
        super(">",typeStorage);
    }

    @Override
    protected int compareStrings(String left, String right) {
        return left.compareTo(right)-1;
    }

    @Override
    protected int compareNumbers(double left, double right) {
        return left>right?0:-1;
    }
}
