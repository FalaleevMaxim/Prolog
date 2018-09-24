package ru.prolog.logic.model.managers.rule;

import ru.prolog.logic.model.managers.AbstractManager;
import ru.prolog.logic.model.managers.option.Option;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.context.program.ProgramContext;
import ru.prolog.logic.runtime.context.rule.BaseRuleContext;
import ru.prolog.logic.runtime.context.rule.RuleContext;
import ru.prolog.logic.runtime.context.rule.TerminatingRuleContext;
import ru.prolog.logic.runtime.values.Value;

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

    @Override
    public RuleContext context(Rule rule, List<Value> args, ProgramContext programContext) {
        RuleContext ruleContext = new BaseRuleContext(rule, args, programContext);
        return decorate(ruleContext);
    }
}