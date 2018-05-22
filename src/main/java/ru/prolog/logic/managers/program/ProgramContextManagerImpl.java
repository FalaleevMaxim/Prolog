package ru.prolog.logic.managers.program;

import ru.prolog.logic.context.program.BaseProgramContext;
import ru.prolog.logic.context.program.ExceptionsCatcherProgramContext;
import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.managers.AbstractManager;
import ru.prolog.logic.model.program.Program;

public class ProgramContextManagerImpl extends AbstractManager<ProgramContext> implements ProgramContextManager{
    {
        addOption(ExceptionsCatcherProgramContext::new);
    }



    @Override
    public ProgramContext create(Program program) {
        return decorate(new BaseProgramContext(program));
    }
}
