package ru.prolog.service;

import ru.prolog.service.backup.BackupManager;
import ru.prolog.service.predicate.PredicateExecutionManager;
import ru.prolog.service.rule.RuleExecutionManager;

public class Managers {
    private final BackupManager backupManager;
    private final PredicateExecutionManager predicateManager;
    private final RuleExecutionManager ruleManager;


    public Managers(BackupManager backupManager, PredicateExecutionManager predicateManager, RuleExecutionManager ruleManager) {
        this.backupManager = backupManager;
        this.predicateManager = predicateManager;
        this.ruleManager = ruleManager;
    }

    public BackupManager getBackupManager() {
        return backupManager;
    }

    public PredicateExecutionManager getPredicateManager() {
        return predicateManager;
    }

    public RuleExecutionManager getRuleManager() {
        return ruleManager;
    }
}
