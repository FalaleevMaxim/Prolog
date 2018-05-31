package ru.prolog.logic.model.predicate;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.exceptions.predicate.*;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.logic.values.Value;

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
            exceptions.add(new PredicateStateException(this, "No rules defined for predicate "+this));
        }
        return exceptions;
    }
}
