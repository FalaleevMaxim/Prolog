package ru.prolog.service.rule;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.rule.Rule;
import ru.prolog.service.option.Option;
import ru.prolog.values.Value;

import java.util.List;

public interface RuleExecutionManager {
    RuleContext executable(Rule rule, List<Value> args, PredicateContext predicateContext);
    RuleContext executable(Rule rule, List<Value> args, ProgramContext programContext);
}
