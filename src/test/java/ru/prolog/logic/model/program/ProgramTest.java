package ru.prolog.logic.model.program;

import org.junit.Test;
import ru.prolog.logic.context.rule.DebuggerRuleContextDecorator;
import ru.prolog.logic.model.program.programs.*;

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
    public void ancestor1(){
        runTest(new Ancestor(true));
    }

    @Test
    public void ancestor2(){
        runTest(new Ancestor(false));
    }

    @Test
    public void namesake1(){
        runTest(new Namesake(0));
    }

    @Test
    public void conc(){
        runTest(new Conc());
    }

    @Test
    public void conc2(){
        runTest(new Conc2());
    }

    @Test
    public void conc3(){
        runTest(new Conc3());
    }

    private void runTest(TestProgram test){
        Program program = test.getProgram();
        System.out.println("-------Program text-------");
        System.out.println(program);
        System.out.println("\n-------Run program-------");
        //Uncomment this to enable debug
        //program.managers().getRuleManager().addOption(DebuggerRuleContextDecorator::new);
        program.fix().run();
    }
}