package ru.prolog.service;

import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.model.predicates.rule.execution.RuleExecution;
import ru.prolog.model.values.Value;

import java.util.List;

public interface RuleExecutableManager {
    RuleExecution executable(Rule rule, List<Value> args);

    interface RuleExecutableOption{

    }
}
