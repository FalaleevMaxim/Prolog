package ru.prolog.managers.rule;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.context.rule.BaseRuleContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.rule.Rule;
import ru.prolog.managers.AbstractManager;
import ru.prolog.managers.option.Option;
import ru.prolog.values.Value;

import java.util.List;

public class RuleContextManagerImpl extends AbstractManager<RuleContext> implements RuleContextManager {
    public RuleContextManagerImpl() {
    }

    public RuleContextManagerImpl(List<Option<RuleContext>> options) {
        super(options);
    }

    @Override
    public RuleContext context(Rule rule, List<Value> args, PredicateContext predicateContext) {
        RuleContext ruleContext = new BaseRuleContext(rule, args, predicateContext);
        return decorate(ruleContext);
    }

    @Override
    public RuleContext context(Rule rule, List<Value> args, ProgramContext programContext) {
        RuleContext ruleContext = new BaseRuleContext(rule, args, programContext);
        return decorate(ruleContext);
    }
}