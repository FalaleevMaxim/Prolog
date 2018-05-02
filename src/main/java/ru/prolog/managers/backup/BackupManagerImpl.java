package ru.prolog.managers.backup;

import ru.prolog.managers.AbstractManager;
import ru.prolog.managers.option.Option;
import ru.prolog.values.Variable;
import ru.prolog.backup.Backup;
import ru.prolog.backup.ValueBackup;

import java.util.List;

public class BackupManagerImpl extends AbstractManager<Backup> implements BackupManager{
    public BackupManagerImpl() {
    }

    public BackupManagerImpl(List<Option<Backup>> options) {
        super(options);
    }

    @Override
    public Backup backup(Variable variable) {
        //If variable has not changed after last backup? return last backup.
        if(!variable.getLastBackup().variableChanged()){
            return variable.getLastBackup();
        }
        Backup backup = new ValueBackup(variable);
        return decorate(backup);
    }

}
