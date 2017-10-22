package ru.prolog.model.predicates.execution.rule;

import ru.prolog.model.Type;
import ru.prolog.model.predicates.execution.predicate.PredicateExecution;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.values.Value;
import ru.prolog.model.values.variables.Variable;

import java.util.Collection;
import java.util.List;

public abstract class BaseRuleExecutionDecorator implements RuleExecution {
    protected RuleExecution decorated;

    public BaseRuleExecutionDecorator(RuleExecution decorated) {
        this.decorated = decorated;
    }

    @Override
    public Rule getRule() {
        return decorated.getRule();
    }

    @Override
    public List<Value> getArgs() {
        return decorated.getArgs();
    }

    @Override
    public Variable getVariable(String name, Type type) {
        return decorated.getVariable(name, type);
    }

    @Override
    public Collection<Variable> getVariables() {
        return decorated.getVariables();
    }

    @Override
    public PredicateExecution getPredicateContext() {
        return decorated.getPredicateContext();
    }
}
