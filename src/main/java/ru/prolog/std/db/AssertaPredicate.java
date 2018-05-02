package ru.prolog.std.db;

import ru.prolog.storage.type.TypeStorage;

public class AssertaPredicate extends AbstractAssertPredicate {
    public AssertaPredicate(TypeStorage typeStorage) {
        super("asserta", typeStorage, true);
    }
}
