package ru.prolog.model.predicate;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.rule.BaseRuleContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.rule.FactRule;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.Value;

import java.util.List;

public class DatabasePredicate extends AbstractPredicate {

    protected DatabasePredicate(String name) {
        super(name);
    }

    public DatabasePredicate(String name, List<String> argTypes, TypeStorage typeStorage) {
        super(name, argTypes, typeStorage);
    }

    @Override
    @SuppressWarnings("Duplicates")
    public int run(PredicateContext context, List<Value> args, int startWith) {
        RuleContext ruleContext = context.getLastExecutedRuleContext();
        if(ruleContext != null){
            if(ruleContext.redo()) return startWith-1;
            context.setLastExecutedRuleContext(null);
        }
        List<FactRule> rules = context.programContext().database().getRules(this);
        for(int i=startWith; i<rules.size();i++){
            if(context.isCut()) return -1;
            ruleContext = new BaseRuleContext(rules.get(i), args, context);
            if(ruleContext.execute()){
                context.setLastExecutedRuleContext(ruleContext);
                return i;
            }
        }
        return -1;
    }
}