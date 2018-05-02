package ru.prolog.managers.program;

import ru.prolog.context.program.BaseProgramContext;
import ru.prolog.context.program.ProgramContext;
import ru.prolog.managers.AbstractManager;
import ru.prolog.model.program.Program;

public class ProgramContextManagerImpl extends AbstractManager<ProgramContext> implements ProgramContextManager{
    @Override
    public ProgramContext create(Program program) {
        return decorate(new BaseProgramContext(program));
    }
}
