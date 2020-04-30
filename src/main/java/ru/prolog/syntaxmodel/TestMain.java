package ru.prolog.syntaxmodel;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.nodes.modules.ProgramNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestMain {
    public static void main(String[] args) {
        final String code = readAllBytes("C:\\Users\\admin\\IdeaProjects\\Prolog\\src\\main\\resources\\sampleCode.pl");
        Lexer lexer = new Lexer(code);
        ProgramNode programNode = new ProgramNode(null);
        boolean parse = programNode.parse(lexer).isOk();
        System.out.println(parse);
    }

    private static String readAllBytes(String filePath)
    {
        String content = "";
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return content;
    }
}
