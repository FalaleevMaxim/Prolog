package ru.prolog.runtime.context;

public interface ExecutionContext {
    /**
     * Executes predicate, getRule or program.
     */
    boolean execute();
}
