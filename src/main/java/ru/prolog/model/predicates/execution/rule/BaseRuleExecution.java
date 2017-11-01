package ru.prolog.model.predicates.execution.rule;


import ru.prolog.WrongTypeException;
import ru.prolog.model.Type;
import ru.prolog.model.predicates.execution.predicate.PredicateExecution;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.values.Value;
import ru.prolog.model.values.variables.Variable;

import java.util.*;

public class BaseRuleExecution implements RuleExecution {
    private Rule rule;
    private List<Value> args;
    private Map<String, Variable> variables;
    private PredicateExecution context;

    public BaseRuleExecution(Rule rule, List<Value> args) {
        this.rule = rule;
        this.args = args;
    }

    public BaseRuleExecution(Rule rule, List<Value> args, PredicateExecution context) {
        this(rule, args);
        this.context = context;
    }

    public Rule getRule() {
        return rule;
    }

    public List<Value> getArgs() {
        return args;
    }

    @Override
    public Variable getVariable(String name, Type type) {
        Variable variable;
        if(variables == null) variables = new HashMap<>();
        if(variables.containsKey(name)){
            variable = variables.get(name);
            if(variable.getType() != type)
                throw new WrongTypeException(
                        "Type of requested variable does not match type of stored variable",
                        type, variable.getType());
            return variable;
        }else{
            variable = type.createVariable(name);
            variables.put(name, variable);
            return variable;
        }
    }

    @Override
    public Collection<Variable> getVariables() {
        if(variables == null) return Collections.emptyList();
        return variables.values();
    }

    @Override
    public PredicateExecution getPredicateContext() {
        return context;
    }

    @Override
    public boolean execute() {
        return rule.run(args, this);
    }
}
