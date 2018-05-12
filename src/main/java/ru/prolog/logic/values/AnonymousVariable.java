package ru.prolog.logic.values;

import ru.prolog.logic.backup.Backup;
import ru.prolog.logic.backup.BackupImpl;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.values.model.ValueModel;
import ru.prolog.logic.values.model.VariableModel;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AnonymousVariable implements Variable {
    private Type type;

    public AnonymousVariable(Type type) {
        this.type = type;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean unify(Value other) {
        return true;
    }

    @Override
    public List<Variable> innerFreeVariables() {
        return Collections.singletonList(this);
    }

    @Override
    public ValueModel toModel() {
        return new VariableModel(type, "_");
    }

    @Override
    public String toString() {
        return "_";
    }

    @Override
    public RuleContext getRuleContext() {
        return null;
    }

    @Override
    public String getName() {
        return "_";
    }

    @Override
    public boolean isFree() {
        return true;
    }

    @Override
    public Backup getLastBackup() {
        return null;
    }

    @Override
    public void setLastBackup(Backup backup) {
    }

    @Override
    public Set<? extends Variable> getRelated() {
        return Collections.emptySet();
    }

    @Override
    public boolean isRelated(Variable variable) {
        return false;
    }

    @Override
    public void addRelated(Variable variable) {

    }

    @Override
    public void removeRelated(Variable variable) {

    }

    @Override
    public void applyValue(Value value) {

    }

    @Override
    public void setFree() {

    }
}
