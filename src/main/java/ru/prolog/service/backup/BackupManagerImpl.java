package ru.prolog.service.backup;

import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.service.AbstractManager;
import ru.prolog.service.option.Option;
import ru.prolog.values.Variable;
import ru.prolog.backup.Backup;
import ru.prolog.backup.ValueBackup;

import java.util.Collection;
import java.util.Collections;
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
