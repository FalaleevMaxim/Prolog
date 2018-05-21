package ru.prolog.util.io;

import ru.prolog.logic.exceptions.PrologRuntimeException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FileOutputDevice implements ErrorListener {
    protected final String fileName;

    public FileOutputDevice(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void prologRuntimeException(PrologRuntimeException e) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))){
            e.printStackTrace(pw);
        } catch (FileNotFoundException ignored) { }
    }

    @Override
    public void runtimeException(RuntimeException e) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))){
            e.printStackTrace(pw);
        } catch (FileNotFoundException ignored) { }
    }

    @Override
    public void print(String s) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))){
            pw.print(s);
        } catch (FileNotFoundException ignored) { }
    }

    @Override
    public void println(String s) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))){
            pw.println(s);
        } catch (FileNotFoundException ignored) { }
    }
}
