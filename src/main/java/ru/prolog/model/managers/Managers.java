package ru.prolog.model.managers;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.AbstractModelObject;
import ru.prolog.model.managers.backup.BackupManager;
import ru.prolog.model.managers.backup.BackupManagerImpl;
import ru.prolog.model.managers.predicate.PredicateContextManager;
import ru.prolog.model.managers.predicate.PredicateContextManagerImpl;
import ru.prolog.model.managers.program.ProgramContextManager;
import ru.prolog.model.managers.program.ProgramContextManagerImpl;
import ru.prolog.model.managers.rule.RuleContextManager;
import ru.prolog.model.managers.rule.RuleContextManagerImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Managers extends AbstractModelObject {
    private ProgramContextManager programManager;
    private BackupManager backupManager;
    private PredicateContextManager predicateManager;
    private RuleContextManager ruleManager;

    public Managers(){
        programManager = new ProgramContextManagerImpl();
        backupManager = new BackupManagerImpl();
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
    public void fixIfOk() {
        programManager.fix();
        backupManager.fix();
        predicateManager.fix();
        ruleManager.fix();
    }

    public ProgramContextManager getProgramManager() {
        return programManager;
    }

    public void setProgramManager(ProgramContextManager programManager) {
        this.programManager = programManager;
    }
}
