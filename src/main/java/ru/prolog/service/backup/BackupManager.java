package ru.prolog.service.backup;

import ru.prolog.model.backup.Backup;
import ru.prolog.values.variables.Variable;

import java.util.List;

public interface BackupManager {
    List<Backup> backup(List<Variable> variables);
}
