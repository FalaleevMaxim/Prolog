package ru.prolog.logic.managers.predicate;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.managers.Manager;
import ru.prolog.logic.values.Value;

import java.util.List;

public interface PredicateContextManager extends Manager<PredicateContext> {
    PredicateContext context(Predicate predicate, List<Value> args, RuleContext ruleContext);
    PredicateContext context(Predicate predicate, List<Value> args, ProgramContext programContext);
}
