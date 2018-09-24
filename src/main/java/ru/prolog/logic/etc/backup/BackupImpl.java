package ru.prolog.logic.etc.backup;

import ru.prolog.logic.model.managers.backup.BackupManager;
import ru.prolog.logic.runtime.values.Variable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BackupImpl implements Backup {
    private Variable variable;
    private Set<? extends Variable> related;
    private Set<Backup> relatedBackups;
    private boolean isFree;

    public BackupImpl(Variable variable, BackupManager backupManager) {
        this.variable = variable;
        isFree = variable.isFree();
        related = new HashSet<>(variable.getRelated());
        variable.setLastBackup(this);
        relatedBackups = related.stream()
                .filter(v -> v!=variable)
                .map(backupManager::backup)
                .collect(Collectors.toSet());
    }

    @Override
    public Variable getVariable() {
        return variable;
    }

    @Override
    public void rollback() {
        if(!variableChanged()) return;
        if(isFree) variable.setFree();
        Set<Variable> toRemove = new HashSet<>();
        for (Variable var : related) {
            if(!related.contains(var)) toRemove.add(var);
        }
        for (Variable var : toRemove) {
            var.removeRelated(variable);
            variable.removeRelated(var);
        }
        variable.setLastBackup(null);
        relatedBackups.forEach(Backup::rollback);
    }

    @Override
    public boolean wasFree() {
        return isFree;
    }

    @Override
    public Set<? extends Variable> getRelated() {
        return related;
    }

    @Override
    public boolean variableChanged() {
        if(isFree && !variable.isFree()) return true;
        for (Variable var : variable.getRelated()) {
            if(!related.contains(var)) return true;
        }
        return false;
    }
}
