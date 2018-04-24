package ru.prolog.values.variables.backup;

import ru.prolog.values.variables.Variable;

public interface Backup {
    Variable getVariable();
    boolean laterThan(Backup other);
    void rollback();
    boolean wasFree();
}
