package ru.prolog.managers.backup;

import ru.prolog.backup.Backup;
import ru.prolog.managers.Manager;
import ru.prolog.values.Variable;

import java.util.List;
import java.util.stream.Collectors;

public interface BackupManager extends Manager<Backup> {
    default List<Backup> backup(List<Variable> variables){
        return variables.stream().map(this::backup).collect(Collectors.toList());
    }
    Backup backup(Variable variable);
}
