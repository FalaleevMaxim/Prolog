package ru.prolog.model.predicate;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.exceptions.predicate.*;
import ru.prolog.model.rule.Rule;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.Value;

import java.util.*;

public class RuleExecutorPredicate extends AbstractPrologPredicate {

    public RuleExecutorPredicate(String name){
        super(name);
    }

    public RuleExecutorPredicate(String name, List<String> argTypes, TypeStorage typeStorage) {
        super(name, argTypes, typeStorage);
    }

    public RuleExecutorPredicate(String name, List<String> argTypes, List<Rule> rules, TypeStorage types) {
        super(name, argTypes, rules, types);
    }

    @Override
    @SuppressWarnings("Duplicates")
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(!fixed) throw new IllegalStateException("Predicate state is not fixed. Call fix() before running it.");
        RuleContext ruleContext = context.getLastExecutedRuleContext();
        if(ruleContext != null){
            if(ruleContext.redo()) return startWith-1;
            context.setLastExecutedRuleContext(null);
        }
        for(int i=startWith; i<rules.size();i++){
            if(context.isCut()) return -1;
            ruleContext = context.getRuleManager().context(rules.get(i), args, context);
            if(ruleContext.execute()){
                context.setLastExecutedRuleContext(ruleContext);
                return i;
            }
        }
        return -1;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = super.exceptions();
        //Checks if predicate has rules
        if(rules.isEmpty()){
            exceptions.add(new PredicateStateException(this, "No rules defined for predicate"));
        }
        return exceptions;
    }
}
