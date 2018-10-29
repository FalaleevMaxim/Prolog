package ru.prolog.runtime.context.program;

import ru.prolog.model.ModelObject;
import ru.prolog.model.program.Program;
import ru.prolog.runtime.RuntimeObject;
import ru.prolog.runtime.context.ExecutionContext;
import ru.prolog.runtime.database.Database;
import ru.prolog.util.io.ErrorListenerHub;
import ru.prolog.util.io.InputDevice;
import ru.prolog.util.io.OutputDeviceHub;
import ru.prolog.util.window.PrologWindowManager;

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

    /**
     * Возвращает менеджер окон, используемый в программе.
     * Может быть {@code null} если окна не поддерживаются в данном исполнителе.
     *
     * @return Используемый менеджер окон или {@code null} если его нет.
     */
    PrologWindowManager getWindowManager();

    /**
     * Устанавливает менеджер окон.
     * Менеджер окон устанавливается исполнителем, использующим библиотеку интерпретатора, перед запуском программы.
     * @param prologWindowManager Менеджер окон.
     */
    void setWindowManager(PrologWindowManager prologWindowManager);
}
