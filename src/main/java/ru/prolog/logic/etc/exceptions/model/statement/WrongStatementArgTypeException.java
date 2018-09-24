package ru.prolog.logic.etc.exceptions.model.statement;

import ru.prolog.logic.etc.exceptions.model.value.ValueStateException;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.rule.Statement;

public class WrongStatementArgTypeException extends ValueStateException{
    public WrongStatementArgTypeException(Predicate predicate, Statement statement, int argNum) {
        this(predicate, statement, argNum,
                "Argument type in statement does not match predicate argument type. " +
                        "Statement argument " + statement.getArgs().get(argNum) + " is not of type " + predicate.getArgTypeNames().get(argNum));
    }

    public WrongStatementArgTypeException(Predicate predicate, Statement statement, int argNum, String message) {
        super(statement.getArgs().get(argNum), message);
    }

}
