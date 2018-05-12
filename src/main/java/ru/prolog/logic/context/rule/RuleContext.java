package ru.prolog.logic.context.rule;

import ru.prolog.logic.context.Executable;
import ru.prolog.logic.context.predicate.PredicateContext;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.context.rule.statements.ExecutedStatements;
import ru.prolog.logic.model.rule.Rule;
import ru.prolog.logic.values.Value;
import ru.prolog.logic.values.Variable;

import java.util.Collection;
import java.util.List;

public interface RuleContext extends Executable {
    Rule getRule();
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
