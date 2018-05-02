package ru.prolog.model.program;

import org.junit.Test;
import ru.prolog.model.program.programs.HelloPredicate;
import ru.prolog.model.program.programs.HelloWorld;
import ru.prolog.model.program.programs.TestProgram;

import static org.junit.Assert.*;

public class ProgramTest {

    @Test
    public void helloWorld(){
        runTest(new HelloWorld());
    }

    @Test
    public void helloPredicate(){
        runTest(new HelloPredicate());
    }

    @Test
    public void ancestor(){

    }

    private void runTest(TestProgram test){
        Program program = test.getProgram();
        System.out.println("-------Program text-------");
        System.out.println(program);
        System.out.println("\n-------Run program-------");
        assertTrue(program.run());
    }
}