package ru.prolog.backup;

import ru.prolog.values.Variable;

public class ValueBackup implements Backup {
    private Variable variable;
    private Backup previous;
    private boolean isFree;

    public ValueBackup(Variable variable) {
        this.variable = variable;
        previous = variable.getLastBackup();
        isFree = variable.isFree();
        variable.setLastBackup(this);
    }

    @Override
    public Variable getVariable() {
        return variable;
    }

    @Override
    public boolean laterThan(Backup other){
        if(previous == null) return false;
        if(other == previous) return true;
        return previous.laterThan(other);
    }

    @Override
    public void rollback() {
        if(isFree) variable.setFree();
    }

    @Override
    public boolean wasFree() {
        return isFree;
    }

    @Override
    public boolean variableChanged() {
        return isFree==variable.isFree();
    }
}
