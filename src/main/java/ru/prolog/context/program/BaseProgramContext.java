package ru.prolog.context.program;

import ru.prolog.model.program.Program;
import ru.prolog.storage.database.Database;
import ru.prolog.storage.database.DatabaseImpl;

import java.util.HashMap;
import java.util.Map;

public class BaseProgramContext implements ProgramContext {
    private final Program program;
    private final Database database;
    private Map<String, Object> contextData;

    public BaseProgramContext(Program program) {
        this.program = program;
        //ToDo: create new database objects as ones in program will be unmodifiable
        this.database = new DatabaseImpl(program.getDatabase());
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
