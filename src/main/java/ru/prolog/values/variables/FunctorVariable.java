package ru.prolog.values.variables;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.type.Type;
import ru.prolog.values.FunctorValue;
import ru.prolog.values.FunctorValueInterface;
import ru.prolog.values.Value;
import ru.prolog.values.variables.backup.Backup;

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
    public FunctorVariableInterface forContext(RuleContext context) {
        FunctorVariableInterface inContext = (FunctorVariableInterface) context.getVariable(name,type);
        if(inContext!=null) return inContext;
        inContext = new FunctorVariable(type, name, context);
        context.addVariable(inContext);
        return inContext;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public List<Variable> innerVariables() {
        if(isFree())
            return Collections.singletonList(this);
        List<Variable> variables = new ArrayList<>();
        variables.add(this);
        variables.addAll(super.innerVariables());
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
}
