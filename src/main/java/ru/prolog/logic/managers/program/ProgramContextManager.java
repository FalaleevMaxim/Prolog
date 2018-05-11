package ru.prolog.logic.managers.program;

import ru.prolog.logic.context.program.ProgramContext;
import ru.prolog.logic.managers.Manager;
import ru.prolog.logic.model.program.Program;

public interface ProgramContextManager extends Manager<ProgramContext>{
    ProgramContext create(Program program);
}
