package ru.prolog.runtime.context.program;

import ru.prolog.model.program.Program;
import ru.prolog.runtime.database.Database;
import ru.prolog.runtime.database.DatabaseImpl;
import ru.prolog.util.io.*;
import ru.prolog.util.window.PrologWindowManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseProgramContext implements ProgramContext {
    private final Program program;
    private final Database database;

    /**
     * Хранит данные, сохраняемые в контексте программы.
     * Поскольку взаимодействовать с этим хранилищем можно не только из программы, работающей в одном потоке,
     * но и из среды, запускающей Пролог-программу, хранилище поддерживает многопоточное обращение.
     */
    private Map<String, Object> contextData = new ConcurrentHashMap<>();
    private InputDevice inputDevice = new Stdin();
    private OutputDeviceHub outputDevices = new OutputDeviceHub(new StdOut());
    private ErrorListenerHub errorListeners = new ErrorListenerHub(new StdErr());
    private PrologWindowManager windowManager;

    public BaseProgramContext(Program program) {
        this.program = program;
        this.database = new DatabaseImpl(program.database());
    }

    @Override
    public Program program() {
        return program;
    }

    @Override
    public Database database() {
        return database;
    }

    @Override
    public Object getContextData(String key) {
        if (contextData == null) return null;
        return contextData.get(key);
    }

    @Override
    public void putContextData(String key, Object data) {
        if (contextData == null) contextData = new HashMap<>();
        contextData.put(key, data);
    }

    /**
     * Запускает программу, передавая себя в качестве контекста.
     * После выполнения очищает и закрывает сохранённые в контексте объекты.
     *
     * @return Результат выполнения программы
     * @see #onFinish()
     */
    @Override
    public boolean execute() {
        boolean res;
        try {
            res = program.run(this);
        } finally {
            onFinish();
        }
        return res;
    }

    @Override
    public InputDevice getInputDevice() {
        return inputDevice;
    }

    @Override
    public void setInputDevice(InputDevice device) {
        inputDevice = device;
    }

    @Override
    public OutputDeviceHub getOutputDevices() {
        return outputDevices;
    }

    @Override
    public ErrorListenerHub getErrorListeners() {
        return errorListeners;
    }

    @Override
    public PrologWindowManager getWindowManager() {
        return windowManager;
    }

    @Override
    public void setWindowManager(PrologWindowManager prologWindowManager) {
        this.windowManager = prologWindowManager;
    }

    /**
     * При завершении программы закрывает все сохранённые в контексте объекты, являющиеся {@link AutoCloseable}.
     * Если при закрытии возникает исключенние, оно выводится на устройства вывода ошибок.
     */
    private void onFinish() {
        for (Object o : contextData.values()) {
            if (o instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) o).close();
                } catch (Exception e) {
                    errorListeners.println("Error closing program context resource:");
                    errorListeners.println(e.toString() + '\n');
                }
            }
        }
    }
}
