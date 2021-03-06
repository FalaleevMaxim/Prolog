package ru.prolog.std.compare;

import ru.prolog.model.storage.type.TypeStorage;

public class MoreEqualsOperatorPredicate extends AbstractCompareOperator {
    public MoreEqualsOperatorPredicate(TypeStorage typeStorage) {
        super(">=", typeStorage);
    }

    @Override
    protected int compareStrings(String left, String right) {
        return left.compareTo(right);
    }

    @Override
    protected int compareNumbers(double left, double right) {
        return left>=right?0:-1;
    }
}
