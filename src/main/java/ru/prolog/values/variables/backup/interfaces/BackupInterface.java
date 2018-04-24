package ru.prolog.values.variables.backup.interfaces;

import ru.prolog.values.variables.Variable;

import java.util.Set;

public interface BackupInterface {
    Variable getVariable();
    Set<? extends Variable> getRelated();
    boolean isRelated(Variable variable);
}
