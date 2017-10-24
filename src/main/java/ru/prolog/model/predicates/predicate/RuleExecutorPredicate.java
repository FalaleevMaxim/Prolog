package ru.prolog.model.predicates.predicate;

import ru.prolog.model.predicates.execution.predicate.PredicateExecution;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.predicates.execution.rule.BaseRuleExecution;
import ru.prolog.model.predicates.execution.rule.RuleExecution;
import ru.prolog.model.values.Value;

import java.util.ArrayList;
import java.util.List;

public class RuleExecutorPredicate extends Predicate {
    private List<Rule> rules;

    public RuleExecutorPredicate(String name, List<String> argTypes, List<Rule> rules) {
        super(name, argTypes);
        this.rules = new ArrayList<>(rules);
    }

    public RuleExecutorPredicate(String name, List<String> argTypes) {
        super(name, argTypes);
        this.rules = new ArrayList<>();
    }

    public void addRule(Rule rule) {
        rules.add(rule);
    }

    @Override
    public int run(PredicateExecution context, List<Value> args, int startWith) {
        for(int i=0; i<rules.size();i++){
            if(context.isCut()) return -1;
            //ToDo: create decorator for RuleExecution
            RuleExecution ruleExecution = new BaseRuleExecution(rules.get(i), args);
            if(ruleExecution.execute()) return i;
        }
        return -1;
    }
}
