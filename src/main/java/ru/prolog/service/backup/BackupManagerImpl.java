package ru.prolog.service.backup;

import ru.prolog.service.AbstractManager;
import ru.prolog.service.option.Option;
import ru.prolog.values.variables.Variable;
import ru.prolog.values.variables.backup.Backup;
import ru.prolog.values.variables.backup.ValueBackup;

import java.util.ArrayList;
import java.util.List;

public class BackupManagerImpl extends AbstractManager<Backup> implements BackupManager{

    public BackupManagerImpl(List<Option<Backup>> options) {
        super(options);
    }

    @Override
    public Backup backup(Variable variable) {
        Backup backup = new ValueBackup(variable);
        return decorate(backup);
    }
}
