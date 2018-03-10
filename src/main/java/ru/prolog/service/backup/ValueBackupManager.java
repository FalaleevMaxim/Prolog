package ru.prolog.service.backup;

import ru.prolog.model.backup.Backup;
import ru.prolog.model.backup.ValueBackup;
import ru.prolog.values.variables.Variable;

import java.util.List;
import java.util.stream.Collectors;

public class ValueBackupManager implements BackupManager {
    @Override
    public List<Backup> backup(List<Variable> variables) {
        return variables.stream().map(ValueBackup::new).collect(Collectors.toList());
    }
}