package ru.prolog.service.backup;

import ru.prolog.backup.Backup;
import ru.prolog.model.ModelObject;
import ru.prolog.service.Manager;
import ru.prolog.service.Managers;
import ru.prolog.values.Variable;

import java.util.List;
import java.util.stream.Collectors;

public interface BackupManager extends Manager<Backup> {
    default List<Backup> backup(List<Variable> variables){
        return variables.stream().map(this::backup).collect(Collectors.toList());
    }
    Backup backup(Variable variable);
}
