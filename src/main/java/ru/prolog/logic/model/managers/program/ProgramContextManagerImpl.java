package ru.prolog.logic.model.managers.program;

import ru.prolog.logic.model.managers.AbstractManager;
import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.runtime.context.program.BaseProgramContext;
import ru.prolog.logic.runtime.context.program.ExceptionsCatcherProgramContext;
import ru.prolog.logic.runtime.context.program.ProgramContext;

public class ProgramContextManagerImpl extends AbstractManager<ProgramContext> implements ProgramContextManager{
    {
        addOption(ExceptionsCatcherProgramContext::new);
    }



    @Override
    public ProgramContext create(Program program) {
        return decorate(new BaseProgramContext(program));
    }
}
