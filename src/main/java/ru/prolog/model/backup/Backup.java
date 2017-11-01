package ru.prolog.model.backup;

import ru.prolog.model.values.variables.Variable;

public interface Backup {
    Variable getVariable();
    boolean laterThan(Backup other);
    void rollback();
    boolean wasFree();
}
