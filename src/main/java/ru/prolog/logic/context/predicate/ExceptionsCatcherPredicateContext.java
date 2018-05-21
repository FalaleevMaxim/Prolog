package ru.prolog.logic.context.predicate;

import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.exceptions.PrologRuntimeException;
import ru.prolog.util.io.ErrorListener;

public class ExceptionsCatcherPredicateContext extends BasePredicateContextDecorator {
    public ExceptionsCatcherPredicateContext(PredicateContext decorated) {
        super(decorated);
    }

    @Override
    public boolean execute() {
        try{
            return decorated.execute();
        }catch (PrologRuntimeException e){
            ErrorListener errorListener = (ErrorListener) programContext().getContextData(ProgramContext.KEY_ERROR_LISTENER);
            if(errorListener!=null) errorListener.prologRuntimeException(e);
            Thread.currentThread().interrupt();
        } catch (RuntimeException e){
            ErrorListener errorListener = (ErrorListener) programContext().getContextData(ProgramContext.KEY_ERROR_LISTENER);
            if(errorListener!=null) errorListener.runtimeException(e);
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
