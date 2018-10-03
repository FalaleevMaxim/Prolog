package ru.prolog.runtime.values;

import ru.prolog.etc.backup.Backup;
import ru.prolog.runtime.context.rule.RuleContext;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public interface Variable extends Value {
    RuleContext getRuleContext();
    String getName();
    boolean isFree();

    Set<? extends Variable> getRelated();
    boolean isRelated(Variable variable);

    default boolean isImplicitlyRelated(Variable variable){
        Set<Variable> seen = new HashSet<>();
        Stack<Variable> traverse = new Stack<>();
        traverse.push(this);
        while(!traverse.isEmpty()){
            Variable var = traverse.pop();
            if(var==variable) return true;
            if(!seen.add(var)) continue;
            traverse.addAll(var.getRelated());
        }
        return false;
    }

    Backup getLastBackup();
    void setLastBackup(Backup backup);

    void addRelated(Variable variable);
    void removeRelated(Variable variable);
    void applyValue(Value value);
    void setFree();
}