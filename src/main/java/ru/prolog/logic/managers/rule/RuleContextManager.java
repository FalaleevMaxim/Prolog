package ru.prolog.logic.managers.rule;

import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.context.rule.RuleContext;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.managers.Manager;
import ru.prolog.logic.values.Value;

import java.util.List;

public interface RuleContextManager extends Manager<RuleContext> {
    RuleContext context(Rule rule, List<Value> args, PredicateContext predicateContext);
    RuleContext context(Rule rule, List<Value> args, ProgramContext programContext);
}
