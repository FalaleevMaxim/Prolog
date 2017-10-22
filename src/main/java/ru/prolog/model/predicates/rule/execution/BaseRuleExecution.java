package ru.prolog.model.predicates.rule.execution;


import ru.prolog.WrongTypeException;
import ru.prolog.model.Type;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.values.Value;
import ru.prolog.model.values.variables.Variable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class BaseRuleExecution implements RuleExecution {
    private Rule rule;
    private List<Value> args;
    private Map<String, Variable> variables;

    public BaseRuleExecution(Rule rule, List<Value> args) {
        this.rule = rule;
        this.args = args;
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
        return variables.values();
    }

    @Override
    public boolean execute() {
        if(!rule.unifyArgs(args, this)) {
            return false;
        }
        return rule.run(this);
    }
}
