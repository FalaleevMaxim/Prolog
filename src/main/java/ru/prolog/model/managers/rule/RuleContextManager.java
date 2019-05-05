package ru.prolog.model.managers.rule;

import ru.prolog.model.managers.Manager;
import ru.prolog.model.rule.Rule;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;

import java.util.List;

public interface RuleContextManager extends Manager<RuleContext> {
    RuleContext context(Rule rule, List<Value> args, PredicateContext predicateContext);
}
