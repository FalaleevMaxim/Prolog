package ru.prolog.logic.values;

import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.backup.Backup;
import ru.prolog.logic.exceprions.FreeVariableException;

import java.util.Iterator;
import java.util.Set;

public interface Variable extends Value {
    RuleContext getRuleContext();
    String getName();
    boolean isFree();

    //При удалении контекста переменной, удаляет эту переменную из связей
    default void dismiss(){
        //Итератор используется чтобы не получать ConcurrentModificationException
        Iterator<? extends Variable> iterator = getRelated().iterator();
        while (iterator.hasNext()){
            Variable variable = iterator.next();
            if(!(variable instanceof AnonymousVariable)) {
                if (this.isFree())
                    throw new FreeVariableException("Return free variable", this);
                if(!unify(variable))
                    throw new IllegalStateException("Error returning value from "+this+" to "+variable);
            }
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