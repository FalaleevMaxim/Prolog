package ru.prolog.logic.runtime.context;

public interface ExecutionContext {
    /**
     * Executes predicate, getRule or program.
     */
    boolean execute();
}
