package ru.prolog.values.list;

import ru.prolog.model.type.exceptions.WrongTypeException;
import ru.prolog.model.type.Type;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.Value;
import ru.prolog.values.Variable;
import ru.prolog.backup.Backup;
import ru.prolog.values.model.ValueModel;
import ru.prolog.values.model.VariableModel;

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
        if(other.getType()!=type) throw new WrongTypeException("Wrong type of value to unify", type, other.getType());
        if(!isFree()) return super.unify(other);
        if(other instanceof ListVariableImpl){
            ListVariableImpl listVariable = (ListVariableImpl)other;
            if(listVariable.isFree()){
                addRelated(listVariable);
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
        variables.add(this);
        variables.addAll(super.innerFreeVariables());
        return variables;
    }

    @Override
    public Set<Variable> getRelated() {
        return related==null? Collections.emptySet():related;
    }

    @Override
    public void addRelated(Variable variable) {
        if(related != null && related.contains(variable)) return;
        if(variable.getType()!=getType())
            throw new WrongTypeException("Wrong type of variable to add as related", type, variable.getType());
        if(related==null) related = new HashSet<>();
        related.add((ListVariableImpl) variable);
        if(!variable.isRelated(this)) variable.addRelated(this);
    }

    @Override
    public void removeRelated(Variable variable) {
        related.remove(variable);
        if(variable.isRelated(this)) variable.removeRelated(this);
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
        ListValue listValue = (ListValue) value;
        if(listValue.isEmpty()){
            isEmpty = true;
        }else{
            this.value = listValue.getValue();
            if(!listValue.isLast()){
                this.next = listValue.tail();
            }
        }
        if(related!=null){
            for(Variable variable : related){
                if(variable.isFree()){
                    variable.applyValue(value);
                }
            }
        }
    }

    @Override
    public Backup getLastBackup() {
        return lastBackup;
    }

    @Override
    public void setLastBackup(Backup backup) {
        this.lastBackup = backup;
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
    public String toString() {
        if(isFree()) return name;
        return super.toString();
    }
}
