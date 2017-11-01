package ru.prolog.model.backup;

import ru.prolog.model.values.variables.Variable;

public class ValueBackup implements Backup {
    private Variable variable;
    Backup previous;
    private boolean isFree;

    public ValueBackup(Variable variable) {
        this.variable = variable;
        isFree = variable.isFree();
    }

    public ValueBackup(Variable variable, Backup previous){
        this(variable);
        this.previous = previous;
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
}
