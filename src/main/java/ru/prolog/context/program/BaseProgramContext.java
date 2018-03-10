package ru.prolog.context.program;

import ru.prolog.model.program.Program;
import ru.prolog.storage.database.Database;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseProgramContext implements ProgramContext {
    private final Program program;
    private final Database database;
    private final Map<String, Database> namedDatabases;
    private Map<String, Object> contextData;

    public BaseProgramContext(Program program, Database database) {
        this.program = program;
        this.database = database;
        namedDatabases = null;
    }

    public BaseProgramContext(Program program, Map<String, Database> namedDatabases) {
        this.program = program;
        this.namedDatabases = namedDatabases;
        this.database = null;
    }

    @Override
    public Program program() {
        return program;
    }

    @Override
    public Database database() {
        //ToDo: add database implementation
        //if(database==null) database =
        return database;
    }

    @Override
    public Database database(String name) {
        if(namedDatabases ==null) return null;
        return namedDatabases.get(name);
    }

    @Override
    public Map<String, Database> databases() {
        if(namedDatabases==null) return null;
        return Collections.unmodifiableMap(namedDatabases);
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
        return program.goal();
    }
}
