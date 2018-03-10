package ru.prolog.service.backup;

import ru.prolog.model.backup.Backup;
import ru.prolog.model.backup.RelatedBackup;
import ru.prolog.values.variables.Variable;

import java.util.List;
import java.util.stream.Collectors;

public class RelatedBackupManager extends AbstractBackupManagerDecrator {
    public RelatedBackupManager(BackupManager decoreted) {
        super(decoreted);
    }

    @Override
    public List<Backup> backup(List<Variable> variables) {
        return decoreted.backup(variables).stream().map(RelatedBackup::new).collect(Collectors.toList());
    }
}
