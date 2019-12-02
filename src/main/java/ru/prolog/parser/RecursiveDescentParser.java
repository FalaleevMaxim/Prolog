package ru.prolog.parser;


import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.nodes.FunctorDefNode;

import static ru.prolog.syntaxmodel.TokenType.LB;
import static ru.prolog.syntaxmodel.TokenType.SYMBOL;

public class RecursiveDescentParser {

    public static FunctorDefNode functorDef(Lexer lexer) {
        FunctorDefNode context = new FunctorDefNode(lexer.getCodeSource());
        Token name = lexer.nextOfType(SYMBOL);
        if (name == null) return null;
        context.setNameToken(name);
        context.insertChild(name, 0);

        Token ignored = lexer.skipIgnored();

        Token lb = lexer.nextOfType(LB);
        if (lb == null) return context;
        context.insertAfter(ignored, name);

        context.addChild(lb);
        context.setLb(lb);

        return context;
    }
}
