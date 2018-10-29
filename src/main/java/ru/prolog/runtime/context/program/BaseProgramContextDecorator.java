package ru.prolog.runtime.context.program;

import ru.prolog.model.program.Program;
import ru.prolog.runtime.database.Database;
import ru.prolog.util.io.ErrorListenerHub;
import ru.prolog.util.io.InputDevice;
import ru.prolog.util.io.OutputDeviceHub;
import ru.prolog.util.window.PrologWindowManager;

public abstract class BaseProgramContextDecorator implements ProgramContext {
    protected final ProgramContext decorated;

    public BaseProgramContextDecorator(ProgramContext decorated) {
        this.decorated = decorated;
    }

    @Override
    public Program program() {
        return decorated.program();
    }

    @Override
    public Database database() {
        return decorated.database();
    }

    @Override
    public Object getContextData(String key) {
        return decorated.getContextData(key);
    }

    @Override
    public void putContextData(String key, Object data) {
        decorated.putContextData(key, data);
    }

    @Override
    public InputDevice getInputDevice() {
        return decorated.getInputDevice();
    }

    @Override
    public void setInputDevice(InputDevice device) {
        decorated.setInputDevice(device);
    }

    @Override
    public OutputDeviceHub getOutputDevices() {
        return decorated.getOutputDevices();
    }

    @Override
    public ErrorListenerHub getErrorListeners() {
        return decorated.getErrorListeners();
    }

    @Override
    public PrologWindowManager getWindowManager() {
        return decorated.getWindowManager();
    }

    @Override
    public void setWindowManager(PrologWindowManager prologWindowManager) {
        decorated.setWindowManager(prologWindowManager);
    }
}
