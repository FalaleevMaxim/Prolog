package ru.prolog.logic.runtime.context.program;

import ru.prolog.logic.model.ModelObject;
import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.runtime.RuntimeObject;
import ru.prolog.logic.runtime.context.ExecutionContext;
import ru.prolog.logic.storage.database.Database;
import ru.prolog.util.io.ErrorListenerHub;
import ru.prolog.util.io.InputDevice;
import ru.prolog.util.io.OutputDeviceHub;

public interface ProgramContext extends ExecutionContext, RuntimeObject {
    String KEY_DEBUG_FILE = "DebugFile";
    String KEY_DEBUG_OUTPUT_DEVICE = "DebugOutput";

    Program program();
    Database database();

    @Override
    default ModelObject model() {
        return program();
    }

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

    InputDevice getInputDevice();
    void setInputDevice(InputDevice device);
    OutputDeviceHub getOutputDevices();
    ErrorListenerHub getErrorListeners();
}
