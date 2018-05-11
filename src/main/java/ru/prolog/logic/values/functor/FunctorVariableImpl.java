package ru.prolog.logic.values.functor;

import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.exceprions.FreeVariableException;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
import ru.prolog.logic.backup.Backup;

import java.util.*;

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
    public boolean unify(Value other) {
        if(isFree()){
            if(other instanceof Variable && ((Variable)other).isFree()){
                Variable var = (Variable) other;
                if(var.getRuleContext()==this.getRuleContext()){
                    throw new FreeVariableException("Attempting to unify two free variables", var);
                }
                addRelated(var);
                var.addRelated(this);
                return true;
            }
            FunctorValue otherFunc = (FunctorValue) other;
            functorName = otherFunc.getFunctorName();
            args = otherFunc.getValue();
            return true;
        }
        return super.unify(other);
    }

    @Override
    @SuppressWarnings("Duplicates")
    public List<Variable> innerFreeVariables() {
        if(isFree())
            return Collections.singletonList(this);
        List<Variable> variables = new ArrayList<>();
        variables.add(this);
        variables.addAll(super.innerFreeVariables());
        return variables;
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
        return name==null;
    }

    @Override
    public Backup getLastBackup() {
        return lastBackup;
    }

    @Override
    public void setLastBackup(Backup backup) {
        lastBackup = backup;
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
    public void addRelated(Variable variable) {
        if(related==null) related = new HashSet<>();
        related.add(variable);
    }

    @Override
    public void removeRelated(Variable variable) {
        related.remove(variable);
    }

    @Override
    public void applyValue(Value value) {
        FunctorValue funcVal = (FunctorValue)value;
        functorName = funcVal.getFunctorName();
        args = funcVal.getValue();
    }

    @Override
    public void setFree() {
        functorName = null;
        args = null;
    }

    @Override
    public String toString() {
        if(isFree()) return name;
        return super.toString();
    }
}
