package ru.prolog.model.exceptions.statement;

import ru.prolog.model.exceptions.ModelStateException;
import ru.prolog.model.rule.Statement;

public class StatementStateException extends ModelStateException {
    public StatementStateException(Statement statement, String message) {
        super(statement, message);
    }

    public Statement getStatement(){
        return (Statement)sender;
    }
}
