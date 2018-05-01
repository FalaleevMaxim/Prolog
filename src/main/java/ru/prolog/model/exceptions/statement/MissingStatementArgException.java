package ru.prolog.model.exceptions.statement;

import ru.prolog.model.predicate.Predicate;
import ru.prolog.model.rule.Statement;
import ru.prolog.model.type.Type;

public class MissingStatementArgException extends StatementStateException{
    private final Predicate predicate;
    private final int argNum;

    public MissingStatementArgException(Statement statement, Predicate predicate, int argNum) {
        this(statement, predicate, argNum,
                String.format("Predicate %s requires %dth argument of type %s",
                        predicate, argNum, predicate.getArgTypeNames().get(argNum)));
    }

    public MissingStatementArgException(Statement statement, Predicate predicate, int argNum, String message) {
        super(statement, message);
        this.predicate = predicate;
        this.argNum = argNum;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public int getArgNum() {
        return argNum;
    }

    public String getPredicateArgTypeName(){
        return predicate.getArgTypeNames().get(argNum);
    }

    public Type getPredicateArgType(){
        return predicate.getTypeStorage().get(getPredicateArgTypeName());
    }
}
