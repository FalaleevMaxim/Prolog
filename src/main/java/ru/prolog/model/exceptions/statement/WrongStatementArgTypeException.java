package ru.prolog.model.exceptions.statement;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.type.Type;
import ru.prolog.values.model.ValueModel;

public class WrongStatementArgTypeException extends StatementStateException{
    private final Predicate predicate;
    private final int argNum;

    public WrongStatementArgTypeException(Predicate predicate, Statement statement, int argNum) {
        this(predicate, statement, argNum,
                "Argument type in statement does not match predicate argument type. " +
                        "Rule argument " + statement.getArgs().get(argNum) + " is not of type " + predicate.getArgTypeNames().get(argNum));
    }

    public WrongStatementArgTypeException(Predicate predicate, Statement statement, int argNum, String message) {
        super(statement, message);
        this.predicate = predicate;
        this.argNum = argNum;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public ValueModel getStatementArg() {
        return getStatement().getArgs().get(argNum);
    }

    public Type getPredicateExpectedType() {
        return predicate.getTypeStorage().get(getPredicateExpectedTypeName());
    }

    public String getPredicateExpectedTypeName() {
        return predicate.getArgTypeNames().get(argNum);
    }

}
