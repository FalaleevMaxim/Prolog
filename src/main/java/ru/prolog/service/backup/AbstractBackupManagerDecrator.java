package ru.prolog.service.backup;

public abstract class AbstractBackupManagerDecrator implements BackupManager{
    protected final BackupManager decoreted;

    public AbstractBackupManagerDecrator(BackupManager decoreted) {
        this.decoreted = decoreted;
    }
}
