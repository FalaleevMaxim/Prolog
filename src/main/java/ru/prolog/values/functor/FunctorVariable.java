package ru.prolog.values.functor;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.type.Type;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;
import ru.prolog.backup.Backup;

import java.util.*;

public class FunctorVariable extends FunctorValue implements FunctorVariableInterface {
    private RuleContext context;
    private Set<Variable> related;
    //Vriable name. Do not confuse with functorName!
    private String name;
    private Backup lastBackup;

    public FunctorVariable(Type type, String name, RuleContext context) {
        super(type, null, null);
        this.name = name;
        this.context = context;
    }

    @Override
    public boolean unify(Value other) {
        if(isFree()){
            if(other instanceof Variable && ((Variable)other).isFree()){
                Variable var = (Variable) other;
                addRelated(var);
                var.addRelated(this);
                return true;
            }
            FunctorValueInterface otherFunc = (FunctorValueInterface) other;
            functorName = otherFunc.getFunctorName();
            subObjects = otherFunc.getValue();
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
        FunctorValueInterface funcVal = (FunctorValueInterface)value;
        functorName = funcVal.getFunctorName();
        subObjects = funcVal.getValue();
    }

    @Override
    public void setFree() {
        functorName = null;
        subObjects = null;
    }

    @Override
    public String toString() {
        if(isFree()) return name;
        return super.toString();
    }
}
