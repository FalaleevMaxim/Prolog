package ru.prolog.service.predicate;

import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.predicate.Predicate;
import ru.prolog.values.Value;

import java.util.List;

public interface PredicateExecutionManager {
    PredicateContext executable(Predicate predicate, List<Value> args, RuleContext ruleContext);
    PredicateContext executable(Predicate predicate, List<Value> args, ProgramContext programContext);

    interface PredicateExecutableOption{

    }
}
