package ru.prolog.etc.backup;

import ru.prolog.runtime.values.Variable;

import java.util.Set;

public interface Backup {
    Variable getVariable();
    void rollback();
    boolean wasFree();
    Set<? extends Variable> getRelated();
    boolean variableChanged();
}
