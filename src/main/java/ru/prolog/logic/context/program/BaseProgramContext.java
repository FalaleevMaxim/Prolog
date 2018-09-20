package ru.prolog.logic.context.program;

import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.storage.database.Database;
import ru.prolog.logic.storage.database.DatabaseImpl;
import ru.prolog.util.io.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseProgramContext implements ProgramContext {
    private final Program program;
    private final Database database;
    private Map<String, Object> contextData = new ConcurrentHashMap<>();
    private InputDevice inputDevice = new Stdin();
    private OutputDeviceHub outputDevices = new OutputDeviceHub(new StdOut());
    private ErrorListenerHub errorListeners = new ErrorListenerHub(new StdErr());

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
