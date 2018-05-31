package ru.prolog.util.io;

public class StdOut implements OutputDevice, StdIO {
    @Override
    public void print(String s) {
        System.out.print(s);
    }

    @Override
    public void println(String s) {
        System.out.println(s);
    }
}
