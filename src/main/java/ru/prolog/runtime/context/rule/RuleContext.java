package ru.prolog.runtime.context.rule;

import ru.prolog.model.ModelObject;
import ru.prolog.model.rule.Rule;
import ru.prolog.runtime.RuntimeObject;
import ru.prolog.runtime.context.ExecutionContext;
import ru.prolog.runtime.context.predicate.PredicateContext;
import ru.prolog.runtime.context.program.ProgramContext;
import ru.prolog.runtime.context.rule.statements.ExecutedStatements;
import ru.prolog.runtime.values.Value;
import ru.prolog.runtime.values.Variable;

import java.util.Collection;
import java.util.List;

public interface RuleContext extends ExecutionContext, RuntimeObject {
    Rule rule();

    @Override
    default ModelObject model() {
        return rule();
    }

    List<Value> getArgs();
    Variable getVariable(String name);
    void addVariable(Variable variable);
    ExecutedStatements getStatements();
    Collection<Variable> getVariables();
    ProgramContext programContext();
    PredicateContext getPredicateContext();
    void rollback();

    // Instead of execute() when need to find new solution
    // Executes last executed statement again.
    // if getRule has no statements, returns false
    boolean redo();
}
