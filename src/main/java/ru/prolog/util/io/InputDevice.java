package ru.prolog.util.io;

import java.io.IOException;

public interface InputDevice {
    String readLine() throws IOException;
    char getch() throws IOException;
}
