package ru.prolog.etc.backup;

import ru.prolog.runtime.values.Variable;

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
