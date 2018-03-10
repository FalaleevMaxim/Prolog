package ru.prolog.model.backup;

import ru.prolog.values.variables.Variable;

import java.util.HashSet;
import java.util.Set;

public class RelatedBackup extends AbstractBackupDecorator {
    Set<? extends Variable> related;

    public RelatedBackup(Backup decorated) {
        super(decorated);
        related = decorated.getVariable().getRelated();
    }

    @Override
    public void rollback() {
        decorated.rollback();
        Set<Variable> toRemove = new HashSet<>();
        for(Variable relatedVariable : decorated.getVariable().getRelated()){
            if(!related.contains(relatedVariable)){
                decorated.getVariable().removeRelated(relatedVariable);
            }
        }
    }
}
