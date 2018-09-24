package ru.prolog.logic.etc.exceptions.model.statement;

import ru.prolog.logic.etc.exceptions.model.value.ValueStateException;
import ru.prolog.logic.model.rule.Statement;
import ru.prolog.util.ToStringUtil;

public class RedundantStatementArgException extends ValueStateException {

    public RedundantStatementArgException(Statement statement, int argNum) {
        this(statement, argNum,
                String.format("Statement %s argument %s is redundant. Predicate %s requires %d arguments.",
                        ToStringUtil.ordinal(argNum), statement.getArgs().get(argNum), statement.getPredicate(), statement.getPredicate().getArgTypeNames().size()));
    }

    public RedundantStatementArgException(Statement sender, int argNum, String message) {
        super(sender.getArgs().get(argNum), message);
    }
}
