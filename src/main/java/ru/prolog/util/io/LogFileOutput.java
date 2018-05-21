package ru.prolog.util.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class LogFileOutput extends FileOutputDevice {
    public LogFileOutput(String fileName) {
        super(fileName);
    }

    @Override
    public void print(String s) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))){
            pw.println(s);
        } catch (FileNotFoundException ignored) { }
    }
}
