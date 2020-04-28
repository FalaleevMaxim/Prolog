package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;

/**
 * Узел с именем типа: примитивного или определённого пользователем
 */
public class TypeNameNode extends AbstractNode {
    /**
     * Токен типа {@link TokenType#PRIMITIVE} или {@link TokenType#SYMBOL}
     */
    private Token typeName;

    public TypeNameNode(AbstractNode parent) {
        super(parent);
    }

    public boolean isPrimitive() {
        return typeName.getTokenType() == TokenType.PRIMITIVE;
    }

    @Override
    protected void clearInternal() {
        typeName = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.PRIMITIVE, TokenType.SYMBOL)) {
            typeName = token;
            addChild(token);
            return true;
        }
        return false;
    }
}
