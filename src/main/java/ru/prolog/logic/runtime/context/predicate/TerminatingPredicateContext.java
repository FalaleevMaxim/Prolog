package ru.prolog.logic.runtime.context.predicate;

import ru.prolog.logic.etc.exceptions.runtime.ProgramInterruptedException;

public class TerminatingPredicateContext extends BasePredicateContextDecorator {
    public TerminatingPredicateContext(PredicateContext decorated) {
        super(decorated);
    }

    @Override
    public boolean execute() {
        checkInterrupted();
        if(!decorated.execute()) return false;
        checkInterrupted();
        return true;
    }

    private void checkInterrupted() {
        if(Thread.interrupted()){
            throw new ProgramInterruptedException();
        }
    }
}
