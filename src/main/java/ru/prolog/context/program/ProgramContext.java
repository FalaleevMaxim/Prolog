package ru.prolog.context.program;

import ru.prolog.context.Executable;
import ru.prolog.context.RuntimeObject;
import ru.prolog.model.program.Program;
import ru.prolog.model.rule.Rule;
import ru.prolog.service.Managers;
import ru.prolog.storage.database.Database;

import java.util.Map;

public interface ProgramContext extends RuntimeObject, Executable {
    Program program();
    Database database();

    /**
     * @return data stored in program context by key or null.
     */
    Object getContextData(String key);

    /**
     * Put sender to context storage. Any predicate in program can access sender by its key using {@link #getContextData(String) getContextData} method.
     * This allows to share data between predicate executions and between different predicates.
     * @param key key for data sender. Must be unique to avoid collisions between different predicates in program
     * @param data any sender to contain state needed for predicate(s) in program scope
     */
    void putContextData(String key, Object data);

    /**
     * Starts goal or requests goal from user
     */
    boolean execute();
}
