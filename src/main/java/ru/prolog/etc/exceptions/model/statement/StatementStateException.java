package ru.prolog.etc.exceptions.model.statement;

import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.rule.Statement;

public class StatementStateException extends ModelStateException {
    public StatementStateException(Statement statement, String message) {
        super(statement, message);
    }

    public Statement getStatement(){
        return (Statement)sender;
    }
}
