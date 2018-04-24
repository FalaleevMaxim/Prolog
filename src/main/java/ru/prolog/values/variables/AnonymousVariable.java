package ru.prolog.values.variables;

import ru.prolog.model.type.Type;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.variables.backup.Backup;
import ru.prolog.values.variables.backup.ValueBackup;
import ru.prolog.values.Value;

import java.util.Collections;
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
    public Value forContext(RuleContext context) {
        return this;
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
    public void dismiss() {

    }

    @Override
    public Backup getLastBackup() {
        return new ValueBackup(this);
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
