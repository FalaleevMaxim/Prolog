package ru.prolog.logic.runtime.context.rule;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.runtime.RuntimeObject;
import ru.prolog.logic.runtime.context.ExecutionContext;
import ru.prolog.logic.runtime.context.predicate.PredicateContext;
import ru.prolog.logic.runtime.context.program.ProgramContext;
import ru.prolog.logic.runtime.context.rule.statements.ExecutedStatements;
import ru.prolog.logic.runtime.values.Value;
import ru.prolog.logic.runtime.values.Variable;

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
