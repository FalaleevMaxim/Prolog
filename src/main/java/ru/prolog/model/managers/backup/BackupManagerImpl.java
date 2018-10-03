package ru.prolog.model.managers.backup;

import ru.prolog.etc.backup.Backup;
import ru.prolog.etc.backup.BackupImpl;
import ru.prolog.model.managers.AbstractManager;
import ru.prolog.model.managers.option.Option;
import ru.prolog.runtime.values.Variable;

import java.util.List;

public class BackupManagerImpl extends AbstractManager<Backup> implements BackupManager{
    public BackupManagerImpl() {
    }

    public BackupManagerImpl(List<Option<Backup>> options) {
        super(options);
    }

    @Override
    public Backup backup(Variable variable) {
        //If variable has not changed after last backup, return last backup.
        if(variable.getLastBackup()!=null && !variable.getLastBackup().variableChanged()){
            return variable.getLastBackup();
        }
        Backup backup = new BackupImpl(variable, this);
        return decorate(backup);
    }
}