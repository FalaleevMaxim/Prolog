package ru.prolog.runtime.values.functor;

import ru.prolog.etc.backup.Backup;
import ru.prolog.model.type.Type;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FunctorVariableImpl extends FunctorValueImpl implements FunctorVariable {
    private RuleContext context;
    private Set<Variable> related;
    //Vriable name. Do not confuse with functorName!
    private String name;
    private Backup lastBackup;

    public FunctorVariableImpl(Type type, String name, RuleContext context) {
        super(type, null, null);
        this.name = name;
        this.context = context;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public boolean unify(Value other) {
        if(!isFree()) return super.unify(other);
        if(other instanceof Variable){
            Variable listVariable = (Variable)other;
            if(listVariable.isFree()){
                addRelated(listVariable);
                listVariable.addRelated(this);
                return true;
            }
        }
        applyValue(other);
        return true;
    }

    @Override
    public List<Variable> innerFreeVariables() {
        if(isFree())
            return Collections.singletonList(this);
        return super.innerFreeVariables();
    }

    @Override
    public RuleContext getRuleContext() {
        return context;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isFree() {
        return functorName==null;
    }

    @Override
    public Set<Variable> getRelated() {
        return related==null ? Collections.emptySet() : related;
    }

    @Override
    public boolean isRelated(Variable variable) {
        return related.contains(variable);
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void addRelated(Variable variable) {
        if(name.equals("_") || variable.getName().equals("_")) return;
        if(related==null) related = new HashSet<>();
        if(isImplicitlyRelated(variable)) return;
        related.add(variable);
        variable.addRelated(this);
    }

    @Override
    public void removeRelated(Variable variable) {
        if(related==null) return;
        related.remove(variable);
        if (variable.isRelated(this)) {
            variable.removeRelated(this);
        }
    }

    @Override
    public void applyValue(Value value) {
        if(!isFree()) return;
        FunctorValue funcVal = (FunctorValue)value;
        functorName = funcVal.getFunctorName();
        args = funcVal.getValue();
        if(related!=null) related.forEach(var->var.applyValue(value));
    }

    @Override
    public void setFree() {
        functorName = null;
        args = null;
    }

    @Override
    public Backup getLastBackup() {
        return lastBackup;
    }

    @Override
    public void setLastBackup(Backup lastBackup) {
        this.lastBackup = lastBackup;
    }

    @Override
    public String toString() {
        if(isFree()) return name;
        return super.toString();
    }
}
