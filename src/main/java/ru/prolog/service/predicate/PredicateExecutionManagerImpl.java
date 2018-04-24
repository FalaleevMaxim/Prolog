package ru.prolog.service.predicate;

import ru.prolog.context.predicate.BasePredicateContext;
import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.service.AbstractManager;
import ru.prolog.service.option.Option;
import ru.prolog.values.Value;

import java.util.List;

public class PredicateExecutionManagerImpl extends AbstractManager<PredicateContext> implements PredicateExecutionManager {
    public PredicateExecutionManagerImpl(List<Option<PredicateContext>> options) {
        super(options);
    }

    @Override
    public PredicateContext executable(Predicate predicate, List<Value> args, RuleContext ruleContext) {
        PredicateContext predicateContext = new BasePredicateContext(predicate, args, ruleContext);
        return decorate(predicateContext);
    }

    @Override
    public PredicateContext executable(Predicate predicate, List<Value> args, ProgramContext programContext) {
        PredicateContext predicateContext = new BasePredicateContext(predicate, args, programContext);
        return decorate(predicateContext);
    }
}
