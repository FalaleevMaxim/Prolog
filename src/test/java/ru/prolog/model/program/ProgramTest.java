package ru.prolog.model.program;

import org.junit.Test;
import ru.prolog.model.program.programs.HelloWorldProgram;

import static org.junit.Assert.*;

public class ProgramTest {
    private Program program;

    @Test
    public void helloWorld(){
        program = new HelloWorldProgram().getProgram();
        System.out.println("-------Program text-------");
        System.out.println(program);
        System.out.println("\n-------Run program-------");
        assertTrue(program.run());
    }

}