package ru.prolog.service;

import ru.prolog.model.backup.Backup;
import ru.prolog.model.values.variables.Variable;

import java.util.List;

public interface BackupManager {
    List<Backup> backup(List<Variable> variables);
}
