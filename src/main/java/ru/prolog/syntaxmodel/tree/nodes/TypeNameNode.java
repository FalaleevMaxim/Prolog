package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

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
    protected ParsingResult parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.PRIMITIVE, TokenType.SYMBOL)) {
            typeName = token;
            addChild(token);
            return OK;
        }
        return FAIL;
    }
}
