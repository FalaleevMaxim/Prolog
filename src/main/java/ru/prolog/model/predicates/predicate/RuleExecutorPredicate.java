package ru.prolog.model.predicates.predicate;

import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.predicates.rule.execution.BaseRuleExecution;
import ru.prolog.model.predicates.rule.execution.RuleExecution;
import ru.prolog.model.values.Value;

import java.util.ArrayList;
import java.util.List;

public class RuleExecutorPredicate extends Predicate {
    List<Rule> rules = new ArrayList<>();

    public RuleExecutorPredicate() {
    }

    public RuleExecutorPredicate(List<Rule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    @Override
    protected int run(List<Value> args, int startWith) {
        for(int i=0; i<rules.size();i++){
            if(cut) return -1;
            //ToDo: create decorator for RuleExecution
            RuleExecution ruleExecution = new BaseRuleExecution(rules.get(i), args);
            if(ruleExecution.execute()) return i;
        }
        return -1;
    }
}
