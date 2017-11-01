package ru.prolog.model.values.variables;

import ru.prolog.model.values.Value;

import java.util.Set;

public interface Variable extends Value {
    String getName();
    boolean isFree();
    void dismiss();

    //ToDo: move to BackupInterface
    Set<? extends Variable> getRelated();
    boolean isRelated(Variable variable);

    //ToDo: move to BackupRestoreInterface
    void addRelated(Variable variable);
    void removeRelated(Variable variable);
    void applyValue(Value value);
    void setFree();
}