package ru.prolog.logic.model.managers.rule;

import ru.prolog.logic.model.managers.Manager;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.context.program.ProgramContext;
import ru.prolog.logic.runtime.context.rule.RuleContext;
import ru.prolog.logic.runtime.values.Value;

import java.util.List;

public interface RuleContextManager extends Manager<RuleContext> {
    RuleContext context(Rule rule, List<Value> args, PredicateContext predicateContext);

    RuleContext context(Rule rule, List<Value> args, ProgramContext programContext);
}
