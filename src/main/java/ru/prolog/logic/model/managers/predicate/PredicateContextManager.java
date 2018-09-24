package ru.prolog.logic.model.managers.predicate;

import ru.prolog.logic.model.managers.Manager;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.rule.Statement;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.context.program.ProgramContext;
import ru.prolog.logic.runtime.context.rule.RuleContext;
import ru.prolog.logic.runtime.values.Value;

import java.util.List;

public interface PredicateContextManager extends Manager<PredicateContext> {
    PredicateContext context(Statement statement, List<Value> args, RuleContext ruleContext);
    PredicateContext context(Predicate predicate, List<Value> args, ProgramContext programContext);
}
