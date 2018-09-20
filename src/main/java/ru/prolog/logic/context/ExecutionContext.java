package ru.prolog.logic.context;

public interface ExecutionContext {
    /**
     * Executes predicate, getRule or program.
     */
    boolean execute();
}
