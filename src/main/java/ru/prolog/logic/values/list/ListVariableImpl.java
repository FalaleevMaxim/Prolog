package ru.prolog.logic.values.list;

import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
import ru.prolog.logic.backup.Backup;
import ru.prolog.logic.model.values.ValueModel;
import ru.prolog.logic.model.values.VariableModel;

import java.util.*;

public class ListVariableImpl extends ListValue implements ListVariable {
    private RuleContext ruleContext;
    //В отличие от ListValue, null значение value может означать как пустой список, так и отсутствие значения переменной
    //isEmpty true означает, что переменная содержит пустой список.
    private boolean isEmpty;
    private Set<Variable> related;
    private String name;
    private Backup lastBackup;

    public ListVariableImpl(Type type, String name, RuleContext ruleContext) {
        super(type);
        this.name = name;
        this.ruleContext = ruleContext;
    }

    @Override
    public Value head(){
        return value;
    }

    @Override
    public boolean unify(Value other) {
        if(!isFree()) return super.unify(other);
        if(other instanceof ListVariableImpl){
            ListVariableImpl listVariable = (ListVariableImpl)other;
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
    public ValueModel toModel() {
        if(isFree())
            return new VariableModel(type, name);
        return super.toModel();
    }

    @Override
    @SuppressWarnings("Duplicates")
    public List<Variable> innerFreeVariables(){
        if(isFree()) return Collections.singletonList(this);
        List<Variable> variables = new ArrayList<>();
        variables.addAll(super.innerFreeVariables());
        return variables;
    }

    @Override
    public Set<Variable> getRelated() {
        return related==null? Collections.emptySet():related;
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
    public void setFree() {
        value = null;
        isEmpty = false;
        next = null;
    }

    @Override
    public boolean isRelated(Variable variable) {
        return related != null && related.contains(variable);
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public boolean isLast() {
        return next==null;
    }

    @Override
    public void applyValue(Value value) {
        if(!isFree()) return;
        ListValue listValue = (ListValue) value;
        if(listValue.isEmpty()){
            isEmpty = true;
        }else{
            this.value = listValue.getValue();
            if(!listValue.isLast()){
                this.next = listValue.tail();
            }
        }

        if(related!=null) related.forEach(var->var.applyValue(value));
    }

    @Override
    public boolean isFree(){
        return !isEmpty && value==null;
    }

    @Override
    public RuleContext getRuleContext() {
        return ruleContext;
    }

    @Override
    public String getName() {
        return name;
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
