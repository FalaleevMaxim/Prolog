package ru.prolog.runtime.context.rule.statements;

import ru.prolog.etc.backup.Backup;
import ru.prolog.runtime.context.predicate.PredicateContext;

import java.util.List;

/**
 * Хранит контекст вызова предиката и бэкапы переменных к состоянию до вызова.
 */
public class ExecutedStatement {
    private final PredicateContext predicateContext;
    private final List<Backup> backupBefore;

    public ExecutedStatement(PredicateContext predicateContext, List<Backup> backupBefore) {
        this.predicateContext = predicateContext;
        this.backupBefore = backupBefore;
    }

    /**
     * Откатывает переменные к состоянию перед вызовом предиката.
     */
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
