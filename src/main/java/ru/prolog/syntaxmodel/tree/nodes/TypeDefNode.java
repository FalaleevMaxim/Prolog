package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;

import static ru.prolog.syntaxmodel.TokenType.*;

public class TypeDefNode extends AbstractNode {
    private Token typeName;
    private Token equalsToken;
    private Token typeNameRight;
    private Token arraySymbol;

    public TypeDefNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        typeName = null;
        equalsToken = null;
        typeNameRight = null;
        arraySymbol = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, SYMBOL)) {
            typeName = token;
            addChild(typeName);
        }
        else return false;

        token = lexer.nextNonIgnored();
        if(ofType(token, EQUALS)) {
            equalsToken = token;
            addChild(equalsToken);
        }
        else return false;

        token = lexer.nextNonIgnored();
        if(ofType(token, SYMBOL)) {
            typeNameRight = token;
            addChild(typeNameRight);
            parseOptional(lexer, this::parseStar);
        } else return false;
        return true;
    }

    private boolean parseStar(Lexer lexer) {
        Token token;
        token = lexer.nextNonIgnored();
        if(ofType(token, STAR_MULTIPLY)) {
            arraySymbol = token;
            addChild(arraySymbol);
            return true;
        }
        return false;
    }
}
