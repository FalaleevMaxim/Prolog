package ru.prolog.model.program;

import ru.prolog.context.predicate.BasePredicateContext;
import ru.prolog.context.rule.BaseRuleContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.predicates.rule.StatementExecutorRule;
import ru.prolog.storage.predicates.PredicateStorage;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.service.backup.BackupManager;

import java.util.Collections;

public class Program {
    private BackupManager backupManager;
    private TypeStorage typeStorage;
    private PredicateStorage predicateStorage;
    private StatementExecutorRule goal;
    private Predicate defaultGoal; //ToDo: do something with default goal

    public boolean goal(){
        if(goal!=null) return new BaseRuleContext(goal, Collections.emptyList()).execute();//ToDo: use execution managers to create context
        return new BasePredicateContext(defaultGoal, Collections.emptyList()).execute();
    }
}