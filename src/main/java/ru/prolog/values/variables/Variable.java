package ru.prolog.values.variables;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.variables.backup.Backup;
import ru.prolog.values.Value;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface Variable extends Value {
    RuleContext getRuleContext();
    String getName();
    boolean isFree();

    //При удалении контекста переменной, удаляет эту переменную из связей
    default void dismiss(){
        Variable first = null;
        //Итератор используется чтобы не получать ConcurrentModificationException
        Iterator<? extends Variable> iterator = getRelated().iterator();
        while (iterator.hasNext()){
            Variable variable = iterator.next();
            if(first==null) first = variable;
            else first.addRelated(variable);
            iterator.remove();
            variable.removeRelated(this);
        }
    }

    Backup getLastBackup();
    void setLastBackup(Backup backup);

    //ToDo: move to BackupInterface
    Set<? extends Variable> getRelated();
    boolean isRelated(Variable variable);

    //ToDo: move to BackupRestoreInterface
    void addRelated(Variable variable);
    void removeRelated(Variable variable);
    void applyValue(Value value);
    void setFree();
}