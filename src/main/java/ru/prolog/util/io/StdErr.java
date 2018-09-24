package ru.prolog.util.io;

import ru.prolog.logic.etc.exceptions.runtime.PrologRuntimeException;

public class StdErr implements ErrorListener, StdIO {
    @Override
    public void prologRuntimeException(PrologRuntimeException e) {
        System.err.println(e);
    }

    @Override
    public void runtimeException(RuntimeException e) {
        e.printStackTrace();
    }

    @Override
    public void print(String s) {
        System.err.print(s);
    }

    @Override
    public void println(String s) {
        System.err.println(s);
    }
}
