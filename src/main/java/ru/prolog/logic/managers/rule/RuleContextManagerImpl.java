package ru.prolog.logic.managers.rule;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.context.rule.BaseRuleContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.managers.AbstractManager;
import ru.prolog.logic.managers.option.Option;
import ru.prolog.logic.values.Value;

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