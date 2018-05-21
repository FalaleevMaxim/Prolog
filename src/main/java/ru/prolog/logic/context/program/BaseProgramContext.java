package ru.prolog.logic.context.program;

import ru.prolog.logic.model.program.Program;
import ru.prolog.logic.storage.database.Database;
import ru.prolog.logic.storage.database.DatabaseImpl;
import ru.prolog.util.io.*;

import java.util.HashMap;
import java.util.Map;

public class BaseProgramContext implements ProgramContext {
    private final Program program;
    private final Database database;
    private Map<String, Object> contextData = new HashMap<>();

    public BaseProgramContext(Program program) {
        this.program = program;
        this.database = new DatabaseImpl(program.database());
        contextData.put(KEY_INPUT_DEVICE, new Stdin());
        contextData.put(KEY_OUTPUT_DEVICE, new OutputDeviceHub(new StdOut()));
        contextData.put(KEY_ERROR_LISTENER, new ErrorListenerHub(new StdErr()));
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
        if(contextData==null) return null;
        return contextData.get(key);
    }

    @Override
    public void putContextData(String key, Object data) {
        if(contextData==null) contextData = new HashMap<>();
        contextData.put(key, data);
    }

    @Override
    public boolean execute() {
        return program.run(this);
    }
}
