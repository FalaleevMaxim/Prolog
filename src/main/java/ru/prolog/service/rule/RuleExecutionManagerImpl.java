package ru.prolog.service.rule;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.context.rule.BaseRuleContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.rule.Rule;
import ru.prolog.service.AbstractManager;
import ru.prolog.service.option.Option;
import ru.prolog.values.Value;

import java.util.List;

public class RuleExecutionManagerImpl extends AbstractManager<RuleContext> implements RuleExecutionManager {
    public RuleExecutionManagerImpl(List<Option<RuleContext>> options) {
        super(options);
    }

    @Override
    public RuleContext executable(Rule rule, List<Value> args, PredicateContext predicateContext) {
        RuleContext ruleContext = new BaseRuleContext(rule, args, predicateContext);
        return decorate(ruleContext);
    }

    @Override
    public RuleContext executable(Rule rule, List<Value> args, ProgramContext programContext) {
        RuleContext ruleContext = new BaseRuleContext(rule, args, programContext);
        return decorate(ruleContext);
    }
}
