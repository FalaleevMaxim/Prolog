package ru.prolog.logic.context.program;

import ru.prolog.logic.context.Executable;
import ru.prolog.logic.context.RuntimeObject;
import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.storage.database.Database;

public interface ProgramContext extends RuntimeObject, Executable {
    String KEY_INPUT_DEVICE = "InputDevice";
    String KEY_OUTPUT_DEVICE = "OutputDevice";
    String KEY_DEBUG_FILE = "DebugFile";
    String KEY_DEBUG_OUTPUT_DEVICE = "DebugOutput";
    String KEY_DEBUG_INPUT_DEVICE = "DebugInput";
    String KEY_ERROR_LISTENER = "ErrorListener";

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
