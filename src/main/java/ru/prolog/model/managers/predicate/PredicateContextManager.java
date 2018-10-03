package ru.prolog.model.managers.predicate;

import ru.prolog.model.managers.Manager;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.Statement;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.RuleContext;
import ru.prolog.runtime.values.Value;

import java.util.List;

public interface PredicateContextManager extends Manager<PredicateContext> {
    PredicateContext context(Statement statement, List<Value> args, RuleContext ruleContext);
    PredicateContext context(Predicate predicate, List<Value> args, ProgramContext programContext);
}
