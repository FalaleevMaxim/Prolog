package ru.prolog.logic.managers.predicate;

import ru.prolog.logic.context.predicate.BasePredicateContext;
import ru.prolog.logic.context.predicate.ExceptionsCatcherPredicateContext;
import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.predicate.TerminatingPredicateContext;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.managers.AbstractManager;
import ru.prolog.logic.managers.option.Option;
import ru.prolog.logic.values.Value;

import java.util.List;

public class PredicateContextManagerImpl extends AbstractManager<PredicateContext> implements PredicateContextManager {
    {
        addOption(ExceptionsCatcherPredicateContext::new);
        addOption(TerminatingPredicateContext::new);
    }

    public PredicateContextManagerImpl() {
    }

    public PredicateContextManagerImpl(List<Option<PredicateContext>> options) {
        super(options);
    }

    @Override
    public PredicateContext context(Predicate predicate, List<Value> args, RuleContext ruleContext) {
        PredicateContext predicateContext = new BasePredicateContext(predicate, args, ruleContext);
        return decorate(predicateContext);
    }

    @Override
    public PredicateContext context(Predicate predicate, List<Value> args, ProgramContext programContext) {
        PredicateContext predicateContext = new BasePredicateContext(predicate, args, programContext);
        return decorate(predicateContext);
    }
}
