package ru.prolog.model.backup.interfaces;

import ru.prolog.values.PrologList;

public interface ListBackupRestoreInterface extends BackupRestoreInterface {
    void setTail(PrologList tail);
}
