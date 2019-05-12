package ru.prolog.syntaxmodel.util;

import ru.prolog.syntaxmodel.tree.recognizers.tokens.StringRecognizer;

public class Test {
    public static void main(String[] args) {
        System.out.println(new StringRecognizer().recognize("\"asd\\u12aa\"asd"));
    }
}
