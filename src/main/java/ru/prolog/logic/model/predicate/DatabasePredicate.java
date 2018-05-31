package ru.prolog.logic.model.predicate;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.exceptions.predicate.IllegalPredicateNameException;
import ru.prolog.logic.model.exceptions.predicate.DbRuleIsNotFactException;
import ru.prolog.logic.model.rule.FactRule;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.model.type.descriptions.CompoundType;
import ru.prolog.logic.model.type.descriptions.Functor;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.util.NameChecker;
import ru.prolog.logic.values.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DatabasePredicate extends AbstractPrologPredicate implements Functor {

    protected DatabasePredicate(String name) {
        super(name);
    }

    public DatabasePredicate(String name, List<String> argTypes, TypeStorage typeStorage) {
        super(name, argTypes, typeStorage);
    }

    public DatabasePredicate(String name, List<String> argTypes, List<Rule> clauses, TypeStorage typeStorage) {
        super(name, argTypes, clauses, typeStorage);
    }

    @Override
    public int run(PredicateContext context, List<Value> args, int startWith) {
        if(!fixed) throw new IllegalStateException("Predicate state is not fixed. Call fix() before running it.");
        List<FactRule> rules = context.programContext().database().getRules(this);

        //correct rule number if rule was removed. ToDo: make better algorithm to detect changes in rules
        int count = rules.size();
        Integer prev_count = (Integer) context.getContextData("rule_count");
        if(prev_count==null) prev_count=0;
        if(count< prev_count)
            startWith-=prev_count-count;
        context.putContextData("rule_count", rules.size());
        for(int i=startWith; i<rules.size();i++){
            RuleContext ruleContext = context.getRuleManager().context(rules.get(i), args, context);
            if(ruleContext.execute()){
                return i;
            }
        }
        return -1;
    }

    @Override
    public Collection<ModelStateException> exceptions() {
        if(fixed) return Collections.emptyList();
        Collection<ModelStateException> exceptions = super.exceptions();
        if(!NameChecker.canBePredicateName(name))
            exceptions.add(new IllegalPredicateNameException(this));
        for(Rule rule : rules){
            if(!(rule instanceof FactRule))
                exceptions.add(new DbRuleIsNotFactException(this, rule));
        }
        return exceptions;
    }

    @Override
    public CompoundType getCompoundType() {
        return typeStorage.getDatabaseType().getCompoundType();
    }

    @Override
    public void setCompoundType(CompoundType type) {

    }
}