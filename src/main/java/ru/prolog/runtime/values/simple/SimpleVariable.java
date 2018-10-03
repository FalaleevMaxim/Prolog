package ru.prolog.runtime.values.simple;

import ru.prolog.etc.backup.Backup;
import ru.prolog.model.type.Type;
import ru.prolog.model.values.SimpleValueModel;
import ru.prolog.model.values.ValueModel;
import ru.prolog.model.values.VariableModel;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;
import ru.prolog.util.ToStringUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Variable of primitive type
 */
public class SimpleVariable extends SimpleValue implements Variable {
    private RuleContext ruleContext;
    private Set<Variable> related;
    private final String name;
    private Backup lastBackup;

    public SimpleVariable(Type type, String name, RuleContext ruleContext) {
        super(type, null);
        this.name = name;
        this.ruleContext = ruleContext;
    }

    public boolean isFree() {
        return value == null;
    }

    public Set<Variable> getRelated() {
        return related == null ? Collections.emptySet() : related;
    }

    @Override
    public boolean unify(Value other) {
        if (other.getValue() != null) {
            if (getValue() != null) {
                return getValue().equals(other.getValue());
            } else {
                applyValue(other);
                return true;
            }
        } else {
            if (getValue() != null) {
                return other.unify(this);
            } else {
                Variable variable = (Variable) other;
                addRelated(variable);
                variable.addRelated(this);
                return true;
            }
        }
    }

    @Override
    public List<Variable> innerFreeVariables() {
        if(isFree()) return Collections.singletonList(this);
        return Collections.emptyList();
    }

    @Override
    public ValueModel toModel() {
        if(isFree())
            return new VariableModel(getType(), getName());
        return new SimpleValueModel(getType(), getValue());
    }

    @Override
    public void applyValue(Value value) {
        if(!this.isFree()) return;
        this.value = value.getValue();
        if(related!=null) related.forEach(var->var.applyValue(value));
    }

    @Override
    public void setFree() {
        value = null;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void addRelated(Variable variable) {
        if(name.equals("_") || variable.getName().equals("_")) return;
        if (related == null) related = new HashSet<>();
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
    public boolean isRelated(Variable variable) {
        return related != null && related.contains(variable);
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
        return ToStringUtil.simpleToString(type, value);
    }
}
