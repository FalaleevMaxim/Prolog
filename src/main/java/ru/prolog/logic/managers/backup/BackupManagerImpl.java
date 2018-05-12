package ru.prolog.logic.managers.backup;

import ru.prolog.logic.managers.AbstractManager;
import ru.prolog.logic.managers.option.Option;
import ru.prolog.logic.values.Variable;
import ru.prolog.logic.backup.Backup;
import ru.prolog.logic.backup.BackupImpl;

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