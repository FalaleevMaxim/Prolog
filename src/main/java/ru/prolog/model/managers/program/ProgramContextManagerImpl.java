package ru.prolog.model.managers.program;

import ru.prolog.model.managers.AbstractManager;
import ru.prolog.model.program.Program;
import ru.prolog.runtime.context.program.BaseProgramContext;
import ru.prolog.runtime.context.program.ExceptionsCatcherProgramContext;
import ru.prolog.runtime.context.program.ProgramContext;

public class ProgramContextManagerImpl extends AbstractManager<ProgramContext> implements ProgramContextManager{
    {
        addOption(ExceptionsCatcherProgramContext::new);
    }



    @Override
    public ProgramContext create(Program program) {
        return decorate(new BaseProgramContext(program));
    }
}
