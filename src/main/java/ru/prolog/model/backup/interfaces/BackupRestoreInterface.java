package ru.prolog.model.backup.interfaces;

import ru.prolog.values.variables.Variable;

public interface BackupRestoreInterface extends BackupInterface {
    void addRelated(Variable variable);
    void removeRelated(Variable variable);
    void setValue(Object value);
    void clearValue();
}
