package ru.prolog.logic.backup;

import ru.prolog.logic.values.Variable;

import java.util.Set;

public interface Backup {
    Variable getVariable();
    void rollback();
    boolean wasFree();
    Set<? extends Variable> getRelated();
    boolean variableChanged();
}
