package ru.prolog.syntaxmodel;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.source.UnmodifiableStringSourceCode;
import ru.prolog.syntaxmodel.tree.nodes.ProgramNode;

public class TestMain {
    public static void main(String[] args) {
        final String code = " domains\narr = integer*  \n  int = integer  ";
        Lexer lexer = new Lexer(new UnmodifiableStringSourceCode(code), code);
        ProgramNode programNode = new ProgramNode(null);
        boolean parse = programNode.parse(lexer);
        System.out.println(parse);
    }
}
