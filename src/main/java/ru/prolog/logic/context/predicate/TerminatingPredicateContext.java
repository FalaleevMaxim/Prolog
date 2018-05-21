package ru.prolog.logic.context.predicate;

public class TerminatingPredicateContext extends BasePredicateContextDecorator {
    public TerminatingPredicateContext(PredicateContext decorated) {
        super(decorated);
    }

    @Override
    public boolean execute() {
        if(Thread.interrupted()){
            Thread.currentThread().interrupt();
            return false;
        }
        if(!decorated.execute()) return false;
        if(Thread.interrupted()){
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }
}
