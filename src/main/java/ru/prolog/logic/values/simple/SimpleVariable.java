package ru.prolog.logic.values.simple;

import ru.prolog.logic.exceprions.FreeVariableException;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.AbstractValue;
import ru.prolog.logic.values.AnonymousVariable;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;
import ru.prolog.logic.backup.Backup;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.values.model.ValueModel;
import ru.prolog.logic.values.model.VariableModel;

import java.util.*;

/**
 * Variable of primitive type
 */
public class SimpleVariable extends AbstractValue implements Variable {
    private RuleContext ruleContext;
    private Set<Variable> related;
    private final String name;
    private Backup lastBackup;

    public SimpleVariable(Type type, String name, RuleContext ruleContext) {
        super(type);
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
            if (other instanceof AnonymousVariable) {
                return true;
            } else {
                if (getValue() != null) {
                    return other.unify(this);
                } else {
                    Variable variable = (Variable) other;
                    if(variable.getRuleContext()==this.getRuleContext()){
                        throw new FreeVariableException("Attempting to unify two free variables", variable);
                    }
                    addRelated(variable);
                    return true;
                }
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
        this.value = value.getValue();
    }

    @Override
    public void setFree() {
        value = null;
    }

    @Override
    public void addRelated(Variable variable) {
        if (related == null) related = new HashSet<>();
        if (related.contains(variable)) return;
        related.add(variable);
        /*if (!variable.isRelated(this)) {
            variable.addRelated(this);
        }*/
    }

    @Override
    public void removeRelated(Variable variable) {
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
    public String toString() {
        if(isFree()) return name;
        return value.toString();
    }

    @Override
    public Backup getLastBackup() {
        return lastBackup;
    }

    public void setLastBackup(Backup backup) {
        this.lastBackup = backup;
    }
}
