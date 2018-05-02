package ru.prolog.managers;

import ru.prolog.backup.RelatedBackup;
import ru.prolog.managers.backup.BackupManagerImpl;
import ru.prolog.managers.predicate.PredicateContextManagerImpl;
import ru.prolog.managers.program.ProgramContextManager;
import ru.prolog.managers.program.ProgramContextManagerImpl;
import ru.prolog.managers.rule.RuleContextManagerImpl;
import ru.prolog.model.ModelObject;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.managers.backup.BackupManager;
import ru.prolog.managers.predicate.PredicateContextManager;
import ru.prolog.managers.rule.RuleContextManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Managers implements ModelObject {
    private ProgramContextManager programManager;
    private BackupManager backupManager;
    private PredicateContextManager predicateManager;
    private RuleContextManager ruleManager;
    private boolean fixed = false;

    public Managers(){
        programManager = new ProgramContextManagerImpl();
        backupManager = new BackupManagerImpl();
        backupManager.addOption(backup -> new RelatedBackup(backup, backupManager));
        predicateManager = new PredicateContextManagerImpl();
        ruleManager = new RuleContextManagerImpl();
    }

    public Managers(BackupManager backupManager, PredicateContextManager predicateManager, RuleContextManager ruleManager) {
        this.backupManager = backupManager;
        this.predicateManager = predicateManager;
        this.ruleManager = ruleManager;
    }

    public void setBackupManager(BackupManager backupManager) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.backupManager = backupManager;
    }

    public void setPredicateManager(PredicateContextManager predicateManager) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.predicateManager = predicateManager;
    }

    public void setRuleManager(RuleContextManager ruleManager) {
        if(fixed) throw new IllegalStateException("State is fixed. You can not change it anymore.");
        this.ruleManager = ruleManager;
    }

    public BackupManager getBackupManager() {
        return backupManager;
    }

    public PredicateContextManager getPredicateManager() {
        return predicateManager;
    }

    public RuleContextManager getRuleManager() {
        return ruleManager;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = new ArrayList<>();

        if(programManager==null) exceptions.add(new ModelStateException(this, "Backup manager is null"));
        else exceptions.addAll(programManager.exceptions());

        if(backupManager==null) exceptions.add(new ModelStateException(this, "Backup manager is null"));
        else exceptions.addAll(backupManager.exceptions());

        if(predicateManager==null) exceptions.add(new ModelStateException(this, "Predicate context manager is null"));
        else exceptions.addAll(predicateManager.exceptions());

        if(ruleManager==null) exceptions.add(new ModelStateException(this, "Rule context manager is null"));
        else exceptions.addAll(ruleManager.exceptions());

        return exceptions;
    }

    @Override
    public ModelObject fix() {
        if(fixed) return this;
        Collection<ModelStateException> exceptions = exceptions();
        if(!exceptions.isEmpty()) throw exceptions.iterator().next();
        fixed = true;
        programManager.fix();
        backupManager.fix();
        predicateManager.fix();
        ruleManager.fix();
        return this;
    }

    public ProgramContextManager getProgramManager() {
        return programManager;
    }

    public void setProgramManager(ProgramContextManager programManager) {
        this.programManager = programManager;
    }
}
