package ru.prolog.model.managers.predicate;

import ru.prolog.model.managers.AbstractManager;
import ru.prolog.model.managers.option.Option;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.Statement;
import ru.prolog.runtime.context.predicate.BasePredicateContext;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.predicate.TerminatingPredicateContext;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;

import java.util.List;

public class PredicateContextManagerImpl extends AbstractManager<PredicateContext> implements PredicateContextManager {
    {
        addOption(TerminatingPredicateContext::new);
    }

    public PredicateContextManagerImpl() {
    }

    public PredicateContextManagerImpl(List<Option<PredicateContext>> options) {
        super(options);
    }

    @Override
    public PredicateContext context(Statement statement, List<Value> args, RuleContext ruleContext) {
        PredicateContext predicateContext = new BasePredicateContext(statement, args, ruleContext);
        return decorate(predicateContext);
    }

    @Override
    public PredicateContext context(Predicate predicate, List<Value> args, ProgramContext programContext) {
        PredicateContext predicateContext = new BasePredicateContext(predicate, args, programContext);
        return decorate(predicateContext);
    }
}
