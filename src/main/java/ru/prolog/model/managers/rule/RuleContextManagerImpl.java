package ru.prolog.model.managers.rule;

import ru.prolog.model.managers.AbstractManager;
import ru.prolog.model.managers.option.Option;
import ru.prolog.model.rule.Rule;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.BaseRuleContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.context.rule.TerminatingRuleContext;
import ru.prolog.runtime.values.Value;

import java.util.List;

public class RuleContextManagerImpl extends AbstractManager<RuleContext> implements RuleContextManager {
    {
        addOption(TerminatingRuleContext::new);
    }

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
}