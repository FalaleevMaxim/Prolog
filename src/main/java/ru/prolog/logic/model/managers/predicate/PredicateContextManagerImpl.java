package ru.prolog.logic.model.managers.predicate;

import ru.prolog.logic.model.managers.AbstractManager;
import ru.prolog.logic.model.managers.option.Option;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.rule.Statement;
import ru.prolog.logic.runtime.context.predicate.BasePredicateContext;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.context.predicate.TerminatingPredicateContext;
import ru.prolog.logic.runtime.context.program.ProgramContext;
import ru.prolog.logic.runtime.context.rule.RuleContext;
import ru.prolog.logic.runtime.values.Value;

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
