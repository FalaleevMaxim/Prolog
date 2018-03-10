package ru.prolog.context.rule;

import ru.prolog.context.Executable;
import ru.prolog.model.Type;
import ru.prolog.context.predicate.BasePredicateContext;
import ru.prolog.context.predicate.PredicateContext;
import ru.prolog.model.predicates.rule.Rule;
import ru.prolog.values.Value;
import ru.prolog.values.variables.Variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface RuleContext extends Executable {
    Rule getRule();
    List<Value> getArgs();
    Variable getVariable(String name, Type type);
    ExecutedStatements getStatements();
    Collection<Variable> getVariables();
    PredicateContext getPredicateContext();
    void rollback();

    // Instead of execute() when need to find new solution
    // Executes last executed statement again.
    // if rule has no statements, returns false
    boolean redo();

    class ExecutedStatements {
        public List<BasePredicateContext> executions = new ArrayList<>();
        public int cutIndex = -1;
        public int currentStatement=0;
    }
}
