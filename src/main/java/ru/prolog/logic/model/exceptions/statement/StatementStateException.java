package ru.prolog.logic.model.exceptions.statement;

import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.rule.Statement;

public class StatementStateException extends ModelStateException {
    public StatementStateException(Statement statement, String message) {
        super(statement, message);
    }

    public Statement getStatement(){
        return (Statement)sender;
    }
}
