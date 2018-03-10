package ru.prolog.model.predicate;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.context.rule.BaseRuleContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.values.Value;

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
    public int run(PredicateContext context, List<Value> args, int startWith) {
        RuleContext ruleContext = context.getLastRuleContext();
        if(ruleContext != null){
            if(ruleContext.redo()) return startWith-1;
            context.setLastRuleContext(null);
        }
        for(int i=startWith; i<rules.size();i++){
            if(context.isCut()) return -1;
            ruleContext = new BaseRuleContext(rules.get(i), args, context);
            if(ruleContext.execute()){
                context.setLastRuleContext(ruleContext);
                return i;
            }
        }
        return -1;
    }
}
