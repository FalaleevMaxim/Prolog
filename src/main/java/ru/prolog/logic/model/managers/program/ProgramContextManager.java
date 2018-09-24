package ru.prolog.logic.model.managers.program;

import ru.prolog.logic.model.managers.Manager;
import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.runtime.context.program.ProgramContext;

public interface ProgramContextManager extends Manager<ProgramContext>{
    ProgramContext create(Program program);
}
