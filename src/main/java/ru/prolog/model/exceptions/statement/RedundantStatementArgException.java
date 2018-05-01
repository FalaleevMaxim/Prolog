package ru.prolog.model.exceptions.statement;

import ru.prolog.model.rule.Statement;
import ru.prolog.values.model.ValueModel;

public class RedundantStatementArgException extends StatementStateException {
    private final int argNum;

    public RedundantStatementArgException(Statement statement, int argNum) {
        this(statement, argNum,
                String.format("Statement %dth argument %s is redundant. Predicate %s requires %d arguments.",
                        argNum, statement.getArgs().get(argNum), statement.getPredicate(), statement.getPredicate().getArgTypeNames().size()));
    }

    public RedundantStatementArgException(Statement sender, int argNum, String message) {
        super(sender, message);
        this.argNum = argNum;
    }

    public ValueModel getArg() {
        return getStatement().getArgs().get(argNum);
    }

    public int getArgNum() {
        return argNum;
    }
}
