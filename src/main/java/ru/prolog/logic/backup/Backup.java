package ru.prolog.logic.backup;

import ru.prolog.logic.values.Variable;

public interface Backup {
    Variable getVariable();
    boolean laterThan(Backup other);
    void rollback();
    boolean wasFree();
    boolean variableChanged();
}
