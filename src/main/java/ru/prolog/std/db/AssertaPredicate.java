package ru.prolog.std.db;

import ru.prolog.model.storage.type.TypeStorage;

public class AssertaPredicate extends AbstractAssertPredicate {
    public AssertaPredicate(TypeStorage typeStorage) {
        super("asserta", typeStorage, true);
    }
}
