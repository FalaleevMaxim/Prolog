package ru.prolog.backup;

import ru.prolog.values.Variable;

public interface Backup {
    Variable getVariable();
    boolean laterThan(Backup other);
    void rollback();
    boolean wasFree();
    boolean variableChanged();
}
