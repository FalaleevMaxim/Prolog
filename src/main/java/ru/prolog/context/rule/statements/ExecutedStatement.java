package ru.prolog.context.rule.statements;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.values.variables.backup.Backup;

import java.util.List;

public class ExecutedStatement {
    public final PredicateContext predicateContext;
    public final List<Backup> backupBefore;

    public ExecutedStatement(PredicateContext predicateContext, List<Backup> backupBefore) {
        this.predicateContext = predicateContext;
        this.backupBefore = backupBefore;
    }

    public void rollback(){
        backupBefore.forEach(Backup::rollback);
    }
}
