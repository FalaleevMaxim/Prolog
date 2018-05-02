package ru.prolog.managers.program;

import ru.prolog.context.program.ProgramContext;
import ru.prolog.managers.Manager;
import ru.prolog.model.program.Program;

public interface ProgramContextManager extends Manager<ProgramContext>{
    ProgramContext create(Program program);
}
