package ru.prolog.logic.runtime.context.rule.statements;

import ru.prolog.logic.etc.backup.Backup;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;

import java.util.List;

public class ExecutedStatement {
    private final PredicateContext predicateContext;
    private final List<Backup> backupBefore;

    public ExecutedStatement(PredicateContext predicateContext, List<Backup> backupBefore) {
        this.predicateContext = predicateContext;
        this.backupBefore = backupBefore;
    }

    public void rollback(){
        backupBefore.forEach(Backup::rollback);
    }

    public PredicateContext getPredicateContext() {
        return predicateContext;
    }

    public List<Backup> getBackup() {
        return backupBefore;
    }
}
