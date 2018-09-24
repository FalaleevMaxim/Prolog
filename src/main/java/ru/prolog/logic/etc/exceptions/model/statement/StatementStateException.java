package ru.prolog.logic.etc.exceptions.model.statement;

import ru.prolog.logic.etc.exceptions.model.ModelStateException;
import ru.prolog.logic.model.rule.Statement;

public class StatementStateException extends ModelStateException {
    public StatementStateException(Statement statement, String message) {
        super(statement, message);
    }

    public Statement getStatement(){
        return (Statement)sender;
    }
}
