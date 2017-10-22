package ru.prolog.model.values.variables;

import ru.prolog.model.predicates.rule.execution.RuleExecution;
import ru.prolog.model.values.Value;

import java.util.Set;

public interface Variable extends Value {
    String getName();
    Set<? extends Variable> getRelated();
    void addRelated(Variable variable);
    void removeRelated(Variable variable);
    boolean isRelated(Variable variable);
    boolean isFree();
    void applyValue(Value value);
    void dismiss();
}
