package ru.prolog.util.io;

import java.io.IOException;

public interface InputDevice {
    String readLine() throws IOException;
    char readChar() throws IOException;
}
