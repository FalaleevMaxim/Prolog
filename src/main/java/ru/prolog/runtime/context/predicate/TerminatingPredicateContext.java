package ru.prolog.runtime.context.predicate;

import ru.prolog.etc.exceptions.runtime.ProgramInterruptedException;
import ru.prolog.model.predicate.PredicateResult;

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
