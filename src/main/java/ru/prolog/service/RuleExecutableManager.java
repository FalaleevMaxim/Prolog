package ru.prolog.service;

import ru.prolog.context.rule.RuleContext;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.values.Value;

import java.util.List;

public interface RuleExecutableManager {
    RuleContext executable(Rule rule, List<Value> args);

    interface RuleExecutableOption{

    }
}
