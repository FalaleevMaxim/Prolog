package ru.prolog.context.rule.statements;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.backup.Backup;

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
