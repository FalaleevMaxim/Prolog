package ru.prolog.runtime.context.predicate;

import ru.prolog.etc.exceptions.runtime.ProgramInterruptedException;
import ru.prolog.model.predicate.PredicateResult;

/**
 * Перед и после вызова предиката проверяет флаг {@link Thread#interrupted()} потока,
 * и если флаг {@code true}, прерывает выполнение программы исключением {@link ProgramInterruptedException}.
 * Это исключение обрабатывается в {@link ru.prolog.runtime.context.program.ExceptionsCatcherProgramContext}.
 */
public class TerminatingPredicateContext extends BasePredicateContextDecorator {
    public TerminatingPredicateContext(PredicateContext decorated) {
        super(decorated);
    }

    @Override
    public PredicateResult execute() {
        checkInterrupted();
        PredicateResult result = decorated.execute();
        if (result == PredicateResult.FAIL) return PredicateResult.FAIL;
        checkInterrupted();
        return result;
    }

    private void checkInterrupted() {
        if(Thread.interrupted()){
            throw new ProgramInterruptedException();
        }
    }
}
