package ru.prolog;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import ru.prolog.logic.storage.type.TypeStorage;
import ru.prolog.parser.ProgramSyntaxListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class Main {
    public static void main(String[] args) throws IOException {

        CharStream input = CharStreams.fromFileName("C:\\Users\\Admin\\IdeaProjects\\Prolog\\src\\main\\resources\\sampleCode.pl");
        PrologLexer lexer = new PrologLexer(input);
        TokenStream tokens = new BufferedTokenStream(lexer);
        PrologParser parser = new PrologParser(tokens);
        ParseTree parseTree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        ProgramSyntaxListener listener = new ProgramSyntaxListener();
        walker.walk(listener, parseTree);
        TypeStorage domains = listener.getProgram().domains();
        //System.out.println(listener.getProgram().fix());
    }
}