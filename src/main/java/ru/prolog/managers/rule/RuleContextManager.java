package ru.prolog.managers.rule;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.rule.Rule;
import ru.prolog.managers.Manager;
import ru.prolog.values.Value;

import java.util.List;

public interface RuleContextManager extends Manager<RuleContext> {
    RuleContext context(Rule rule, List<Value> args, PredicateContext predicateContext);
    RuleContext context(Rule rule, List<Value> args, ProgramContext programContext);
}
