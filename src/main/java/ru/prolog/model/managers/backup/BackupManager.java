package ru.prolog.model.managers.backup;

import ru.prolog.etc.backup.Backup;
import ru.prolog.model.managers.Manager;
import ru.prolog.runtime.values.Variable;

import java.util.List;
import java.util.stream.Collectors;

public interface BackupManager extends Manager<Backup> {
    default List<Backup> backup(List<Variable> variables){
        return variables.stream().map(this::backup).collect(Collectors.toList());
    }
    Backup backup(Variable variable);
}
