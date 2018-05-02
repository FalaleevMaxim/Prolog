package ru.prolog.std.db;

import ru.prolog.storage.type.TypeStorage;

public class AssertzPredicate extends AbstractAssertPredicate {
    public AssertzPredicate(TypeStorage typeStorage) {
        super("assertz", typeStorage, false);
    }
}