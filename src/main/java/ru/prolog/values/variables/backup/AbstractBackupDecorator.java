package ru.prolog.values.variables.backup;

import ru.prolog.values.variables.Variable;

public abstract class AbstractBackupDecorator implements Backup{
    protected Backup decorated;

    public AbstractBackupDecorator(Backup decorated) {
        this.decorated = decorated;
        decorated.getVariable().setLastBackup(this);
    }

    @Override
    public Variable getVariable() {
        return decorated.getVariable();
    }

    @Override
    public boolean laterThan(Backup other) {
        return decorated.laterThan(other);
    }

    @Override
    public boolean wasFree() {
        return decorated.wasFree();
    }
}
