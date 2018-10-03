package ru.prolog.runtime.values.list;

import ru.prolog.etc.backup.Backup;
import ru.prolog.model.type.Type;
import ru.prolog.model.values.ValueModel;
import ru.prolog.model.values.VariableModel;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public ValueModel toModel() {
        if(isFree())
            return new VariableModel(type, name);
        return super.toModel();
    }

    @Override
    public List<Variable> innerFreeVariables(){
        if(isFree()) return Collections.singletonList(this);
        return super.innerFreeVariables();
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
        head = null;
        isEmpty = false;
        tail = null;
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
        return tail ==null;
    }

    @Override
    public void applyValue(Value value) {
        if(!isFree()) return;
        ListValue listValue = (ListValue) value;
        if(listValue.isEmpty()){
            isEmpty = true;
        }else{
            this.head = listValue.getValue();
            if(!listValue.isLast()){
                this.tail = listValue.tail();
            }
        }

        if(related!=null) related.forEach(var->var.applyValue(value));
    }

    @Override
    public boolean isFree(){
        return !isEmpty && head ==null;
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
