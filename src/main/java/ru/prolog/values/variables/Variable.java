package ru.prolog.values.variables;

import ru.prolog.model.backup.Backup;
import ru.prolog.values.Value;

import java.util.Set;

public interface Variable extends Value {
    String getName();
    boolean isFree();
    void dismiss();

    Backup getLastBackup();
    void setLastBackup(Backup backup);

    //ToDo: move to BackupInterface
    Set<? extends Variable> getRelated();
    boolean isRelated(Variable variable);

    //ToDo: move to BackupRestoreInterface
    void addRelated(Variable variable);
    void removeRelated(Variable variable);
    void applyValue(Value value);
    void setFree();
}