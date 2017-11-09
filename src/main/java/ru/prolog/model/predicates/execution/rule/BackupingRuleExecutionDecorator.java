package ru.prolog.model.predicates.execution.rule;

import ru.prolog.model.backup.Backup;
import ru.prolog.model.backup.ValueBackup;
import ru.prolog.model.values.variables.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BackupingRuleExecutionDecorator extends BaseRuleExecutionDecorator {
    private List<Backup> backups;

    public BackupingRuleExecutionDecorator(RuleExecution decorated) {
        super(decorated);
    }

    @Override
    public boolean execute() {
        backups = decorated.getArgs().stream()
                .filter(value -> value instanceof Variable)
                .map(value -> (Variable)value)
                .map(ValueBackup::new)
                .collect(Collectors.toList());
        if(decorated.execute()){
            return true;
        }
        else{
            backups.forEach(Backup::rollback);
            return false;
        }
    }
}
