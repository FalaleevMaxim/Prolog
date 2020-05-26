package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Named;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

/**
 * Узел с именем типа: примитивного или определённого пользователем
 */
public class TypeNameNode extends AbstractNode implements Named {
    /**
     * Токен типа {@link TokenType#PRIMITIVE} или {@link TokenType#SYMBOL}
     */
    private Token name;

    public TypeNameNode(AbstractNode parent) {
        super(parent);
    }

    public boolean isPrimitive() {
        return name.getTokenType() == TokenType.PRIMITIVE;
    }

    @Override
    protected void clearInternal() {
        name = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.PRIMITIVE, TokenType.SYMBOL)) {
            name = token;
            addChild(token);
            return OK;
        }
        return FAIL;
    }

    @Override
    public Token getName() {
        return name;
    }
}
