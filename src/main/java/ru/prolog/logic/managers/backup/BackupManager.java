package ru.prolog.logic.managers.backup;

import ru.prolog.logic.backup.Backup;
import ru.prolog.logic.managers.Manager;
import ru.prolog.logic.values.Variable;

import java.util.List;
import java.util.stream.Collectors;

public interface BackupManager extends Manager<Backup> {
    default List<Backup> backup(List<Variable> variables){
        return variables.stream().map(this::backup).collect(Collectors.toList());
    }
    Backup backup(Variable variable);
}
