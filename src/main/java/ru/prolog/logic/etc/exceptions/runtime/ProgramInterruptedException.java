package ru.prolog.logic.etc.exceptions.runtime;

public class ProgramInterruptedException extends RuntimeException{
    public ProgramInterruptedException() {
    }

    public ProgramInterruptedException(String message) {
        super(message);
    }
}
