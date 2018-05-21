package ru.prolog.logic.std.compare;

import ru.prolog.logic.storage.type.TypeStorage;

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
