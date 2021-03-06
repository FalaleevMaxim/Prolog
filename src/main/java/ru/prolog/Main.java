package ru.prolog;

import ru.prolog.compiler.CompileException;
import ru.prolog.compiler.PrologCompiler;
import ru.prolog.etc.exceptions.model.ModelStateException;
import ru.prolog.model.program.Program;

import java.io.IOException;
import java.util.Collection;

@SuppressWarnings("Duplicates")
public class Main {
    public static void main(String[] args) throws IOException {
        if(args.length==0){
            System.err.println("Arguments: <Code file> [-log <log output file>]");
            return;
        }

        String fileName = args[0].replace("\"", "");
        boolean debug = args.length>1 && args[1].equals("-d");
        String debugFile = null;
        if(debug){
            if(args.length>2) debugFile = args[2].replace("\"", "");
            else{
                System.err.println("-d requires debug output file name");
                return;
            }
        }

        PrologCompiler compiler = new PrologCompiler(fileName, debugFile);

        Program program = compiler.compileProgram();
        for (CompileException exception : compiler.getExceptions()) {
            System.err.println(exception);
        }
        if(program==null) return;

        Collection<ModelStateException> programExceptions = program.exceptions();
        for (CompileException exception : programExceptions) {
            System.err.println(exception);
        }
        if(!programExceptions.isEmpty()) return;

        System.out.println("---------Program text---------");
        System.out.println(program.fix());
        System.out.println("\n---------Run program---------");
        program.run();
    }
}