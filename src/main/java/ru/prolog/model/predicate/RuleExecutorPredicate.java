package ru.prolog.model.predicate;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.rule.BaseRuleContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.ModelBuilder;
import ru.prolog.model.rule.Rule;
import ru.prolog.service.rule.RuleExecutionManager;
import ru.prolog.storage.type.TypeStorage;
import ru.prolog.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RuleExecutorPredicate extends AbstractPredicate {
    private List<Rule> rules;

    private RuleExecutorPredicate(String name){
        super(name);
    }

    private RuleExecutorPredicate(String name, List<String> argTypes, List<Rule> rules, TypeStorage types) {
        super(name, argTypes, types);
        this.rules = new ArrayList<>(rules);
    }

    @Override
    @SuppressWarnings("Duplicates")
    public int run(PredicateContext context, List<Value> args, int startWith) {
        RuleContext ruleContext = context.getLastExecutedRuleContext();
        if(ruleContext != null){
            if(ruleContext.redo()) return startWith-1;
            context.setLastExecutedRuleContext(null);
        }
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

    public static class Builder implements ModelBuilder<RuleExecutorPredicate>{
        private String name;
        private List<String> argTypes = new ArrayList<>();
        private TypeStorage typeStorage;
        private List<Rule.RuleBuilder<? extends Rule>> rules = new ArrayList<>();

        private RuleExecutorPredicate created;

        public Builder(String name, TypeStorage typeStorage){
            this.name = name;
            this.typeStorage = typeStorage;
        }

        public void addArgType(String type){
            if(created!=null) throw new IllegalStateException("Predicate already created and can not be changed");
            if(typeStorage==null) throw new IllegalStateException("No TypeStorage defined to get arg types from");
            if(typeStorage.get(type)==null) throw new IllegalArgumentException("Type by functorName does not exist in TypeStorage");
            argTypes.add(type);
        }

        public void addRule(Rule.RuleBuilder<? extends Rule> rule){
            if(created!=null) throw new IllegalStateException("Predicate already created and can not be changed");
            rules.add(rule);
        }

        @Override
        public RuleExecutorPredicate create() {
            if(name==null) throw new IllegalStateException("Name not defined");
            if(typeStorage==null && !argTypes.isEmpty()) throw new IllegalArgumentException("Predicate has arg types but typeStorage not defined");

            if(rules.isEmpty()) throw new IllegalStateException("No rules for predicate");
            created = new RuleExecutorPredicate(name, argTypes, rules.stream().map(ModelBuilder::create).collect(Collectors.toList()), typeStorage);
            for(Rule.RuleBuilder<? extends Rule> rule : rules){
                rule.setPredicate(created);
                rule.close();
            }
            return created;
        }

        public RuleExecutorPredicate get() {
            if(created==null) return create();
            return created;
        }
    }
}
