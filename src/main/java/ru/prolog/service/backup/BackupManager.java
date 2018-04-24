package ru.prolog.service.backup;

import ru.prolog.values.variables.backup.Backup;
import ru.prolog.values.variables.Variable;

import java.util.List;
import java.util.stream.Collectors;

public interface BackupManager {
    default List<Backup> backup(List<Variable> variables){
        return variables.stream().map(this::backup).collect(Collectors.toList());
    }
    Backup backup(Variable variable);
}
