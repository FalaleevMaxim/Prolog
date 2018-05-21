package ru.prolog.logic.model.exceptions.statement;

import ru.prolog.logic.model.exceptions.value.ValueStateException;
import ru.prolog.logic.model.predicate.Predicate;
import ru.prolog.logic.model.rule.Statement;
import ru.prolog.logic.model.type.Type;
import ru.prolog.logic.model.values.ValueModel;

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
