package ru.prolog.logic.context.predicate;

import ru.prolog.logic.context.program.ExceptionsCatcherProgramContext;

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
            throw new ExceptionsCatcherProgramContext.ProgramInterruptedException();
        }
    }
}
