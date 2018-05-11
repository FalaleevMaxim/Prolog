package ru.prolog.parser;

import org.antlr.v4.runtime.tree.ParseTree;

public class ParseException extends RuntimeException {
    private final ParseTree cause;

    public ParseException(ParseTree cause, String message) {
        super(message);
        this.cause = cause;
    }
}
