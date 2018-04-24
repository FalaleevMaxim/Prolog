package ru.prolog.context.program;

import ru.prolog.context.Executable;
import ru.prolog.model.program.Program;
import ru.prolog.service.Managers;
import ru.prolog.storage.database.Database;

import java.util.Map;

public interface ProgramContext extends Executable {
    Program program();

    /**
     * Returns only database in program if it does not have functorName
     * To get named databases use {@link #databases() databases} method.
     * To get database by functorName use {@link #database(String) database(String)} overload
     * @return unnamed database or null if databases are named
     */
    Database database();

    /**
     * Returns database by functorName
     * @param name database functorName
     * @return database by functorName or null.
     */
    Database database(String name);

    /**
     * @return all databases and their names
     */
    Map<String, Database> databases();

    /**
     * @return data stored in program context by key or null.
     */
    Object getContextData(String key);

    /**
     * Put object to context storage. Any predicate in program can access object by its key using {@link #getContextData(String) getContextData} method.
     * This allows to share data between predicate executions and between different predicates.
     * @param key key for data object. Must be unique to avoid collisions between different predicates in program
     * @param data any object to contain state needed for predicate(s) in program scope
     */
    void putContextData(String key, Object data);

    /**
     * Starts goal or requests goal from user
     */
    boolean execute();
}
