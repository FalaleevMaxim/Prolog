package ru.prolog.runtime.context.program;

import ru.prolog.etc.exceptions.runtime.ProgramInterruptedException;
import ru.prolog.etc.exceptions.runtime.PrologRuntimeException;
import ru.prolog.util.io.ErrorListener;

public class ExceptionsCatcherProgramContext extends BaseProgramContextDecorator {

    public ExceptionsCatcherProgramContext(ProgramContext decorated) {
        super(decorated);
    }

    @Override
    public boolean execute() {
        try{
            return decorated.execute();
        }catch (PrologRuntimeException e){
            ErrorListener errorListener = getErrorListeners();
            errorListener.prologRuntimeException(e);
            errorListener.println("Program finished with Prolog runtime exception");
            return false;
        } catch (ProgramInterruptedException e){
            ErrorListener errorListener = getErrorListeners();
            errorListener.print("Program terminated");
            if(e.getMessage()==null) errorListener.println(": "+e.getMessage());
            else errorListener.print("\n");
            return false;
        } catch (RuntimeException e){
            ErrorListener errorListener = getErrorListeners();
            errorListener.runtimeException(e);
            errorListener.println("Program finished with Java runtime exception");
            return false;
        }
    }
}
