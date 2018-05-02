package ru.prolog.std.db;

import ru.prolog.storage.type.TypeStorage;

public class AssertPredicate extends AbstractAssertPredicate {
    public AssertPredicate(TypeStorage typeStorage) {
        super("assert", typeStorage, false);
    }
}
