package ru.prolog;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import ru.prolog.compiler.CompileException;
import ru.prolog.compiler.PrologCompiler;
import ru.prolog.logic.model.exceptions.ModelStateException;
import ru.prolog.logic.model.program.Program;

import java.io.IOException;
import java.util.Collection;

@SuppressWarnings("Duplicates")
public class Main {
    public static void main(String[] args) throws IOException {
        CharStream input = CharStreams.fromFileName("C:\\Users\\Admin\\IdeaProjects\\Prolog\\src\\main\\resources\\sampleCode.pl");
        PrologLexer lexer = new PrologLexer(input);
        TokenStream tokens = new BufferedTokenStream(lexer);
        PrologParser parser = new PrologParser(tokens);
        PrologCompiler compiler = new PrologCompiler();
        parser.removeErrorListeners();
        parser.addErrorListener(compiler);
        lexer.removeErrorListeners();
        lexer.addErrorListener(compiler);
        ParseTree parseTree = parser.program();
        if(printExceptions(compiler)) return;

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(compiler, parseTree);
        if(printExceptions(compiler)) return;

        Program program = compiler.getProgram();
        Collection<ModelStateException> exceptions = program.exceptions();
        for (ModelStateException e : exceptions) {
            System.err.println(e);
        }
        if(!exceptions.isEmpty()) return;

        System.out.println("---------Program text---------");
        System.out.println(compiler.getProgram().fix());
        System.out.println("\n---------Run program---------");
        program.run();
    }

    private static boolean printExceptions(PrologCompiler compiler) {
        for (CompileException compileException : compiler.exceptions()) {
            System.err.println(compileException);
        }
        return !compiler.exceptions().isEmpty();
    }
}