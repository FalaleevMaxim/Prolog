package ru.prolog.model.predicates.rule.execution;

import ru.prolog.model.Type;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.values.Value;
import ru.prolog.model.values.variables.Variable;

import java.util.Collection;
import java.util.List;

public interface RuleExecution {
    Rule getRule();
    List<Value> getArgs();
    Variable getVariable(String name, Type type);
    Collection<Variable> getVariables();

    boolean execute();
}
