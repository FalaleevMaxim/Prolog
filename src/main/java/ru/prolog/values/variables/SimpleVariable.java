package ru.prolog.values.variables;

import ru.prolog.model.type.Type;
import ru.prolog.values.variables.backup.Backup;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.AbstractValue;
import ru.prolog.values.Value;

import java.util.*;

/**
 * Variable of base type
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
                    addRelated(variable);
                    return true;
                }
            }
        }
    }

    @Override
    public Value forContext(RuleContext context) {
        Variable inContext = context.getVariable(name, type);
        if(inContext!=null) return inContext;
        inContext = new SimpleVariable(type, name, ruleContext);
        context.addVariable(inContext);
        return inContext;
    }

    @Override
    public List<Variable> innerVariables() {
        return Collections.singletonList(this);
    }

    @Override
    public void applyValue(Value value) {
        this.value = value.getValue();
        if (related != null) {
            for (Variable variable : related) {
                if (variable.isFree()) {
                    //ToDo: apply value only to variables from same context
                    variable.applyValue(value);
                }
            }
        }
    }

    @Override
    public void setFree() {
        value = null;
    }

    @Override
    public void addRelated(Variable variable) {
        if (related != null && related.contains(variable)) return;
        if (related == null) related = new HashSet<>();
        related.add(variable);
        if (!variable.isRelated(this)) {
            variable.addRelated(this);
        }
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
        this.lastBackup = lastBackup;
    }
}
