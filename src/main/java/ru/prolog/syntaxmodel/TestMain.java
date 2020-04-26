package ru.prolog.syntaxmodel;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.nodes.ProgramNode;

public class TestMain {
    public static void main(String[] args) {
        final String code = " domains\nfoo = foo(integer) ; bar(integer, foo)\narr = integer*  \n  int = integer ";
        Lexer lexer = new Lexer(code);
        ProgramNode programNode = new ProgramNode(null);
        boolean parse = programNode.parse(lexer);
        System.out.println(parse);
    }
}
