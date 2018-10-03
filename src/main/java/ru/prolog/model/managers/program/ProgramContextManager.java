package ru.prolog.model.managers.program;

import ru.prolog.model.managers.Manager;
import ru.prolog.model.program.Program;
import ru.prolog.runtime.context.program.ProgramContext;

public interface ProgramContextManager extends Manager<ProgramContext>{
    ProgramContext create(Program program);
}
