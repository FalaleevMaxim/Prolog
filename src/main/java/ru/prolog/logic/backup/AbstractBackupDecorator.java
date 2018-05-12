package ru.prolog.logic.backup;

import ru.prolog.logic.values.Variable;

public abstract class AbstractBackupDecorator implements Backup{
    protected Backup decorated;

    protected AbstractBackupDecorator(Backup decorated) {
        this.decorated = decorated;
        decorated.getVariable().setLastBackup(this);
    }

    @Override
    public Variable getVariable() {
        return decorated.getVariable();
    }

    @Override
    public boolean wasFree() {
        return decorated.wasFree();
    }
}
