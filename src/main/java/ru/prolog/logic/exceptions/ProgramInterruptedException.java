package ru.prolog.logic.exceptions;

public class ProgramInterruptedException extends RuntimeException{
    public ProgramInterruptedException() {
    }

    public ProgramInterruptedException(String message) {
        super(message);
    }
}
