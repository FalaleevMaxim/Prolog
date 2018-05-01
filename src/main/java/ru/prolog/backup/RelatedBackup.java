package ru.prolog.backup;

import ru.prolog.service.backup.BackupManager;
import ru.prolog.values.Variable;

import java.util.HashSet;
import java.util.Set;

public class RelatedBackup extends AbstractBackupDecorator {
    private Set<? extends Variable> related;
    private Set<Backup> relatedBackups;

    public RelatedBackup(Backup decorated, BackupManager manager) {
        super(decorated);
        related = new HashSet<>(decorated.getVariable().getRelated());
        relatedBackups = new HashSet<>();
        for(Variable v : related){
            relatedBackups.add(manager.backup(v));
        }
    }

    @Override
    public void rollback() {
        decorated.rollback();
        relatedBackups.forEach(Backup::rollback);
        //Collect related variables which should me removed
        //Can not remove them in same loop because of possible ConcurrentModificationException
        Set<Variable> toRemove = new HashSet<>();
        for(Variable relatedVariable : getVariable().getRelated()){
            if(!related.contains(relatedVariable)){
                toRemove.add(relatedVariable);
            }
        }
        for(Variable remove : toRemove) {
            getVariable().removeRelated(remove);
        }
    }

    @Override
    public boolean variableChanged() {
        return decorated.variableChanged() && related.equals(getVariable().getRelated());
    }
}
