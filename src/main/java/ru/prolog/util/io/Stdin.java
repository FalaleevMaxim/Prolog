package ru.prolog.util.io;

import java.io.IOException;
import java.util.Scanner;

public class Stdin implements InputDevice, StdIO{
    private Scanner sc = new Scanner(System.in);

    @Override
    public String readLine() {
        return sc.nextLine();
    }

    @Override
    public char readChar() throws IOException {
        return sc.nextLine().charAt(0);
    }
}
