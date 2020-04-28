package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;

import static ru.prolog.syntaxmodel.TokenType.*;

public class BinaryMathExprNode extends AbstractNode {
    /**
     * Число слева
     */
    Token left;

    /**
     * Оператор '+', '-', '*', '/', 'div', 'mod'
     */
    Token operator;

    /**
     * Число справа
     */
    Token right;

    public BinaryMathExprNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        left = null;
        operator = null;
        right = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, INTEGER, REAL, VARIABLE)) return false;
        left = token;
        addChild(left);

        token = lexer.nextNonIgnored();
        if(!ofType(token, PLUS, MINUS, DIVIDE, STAR_MULTIPLY, DIV, MOD)) return false;
        operator = token;
        addChild(operator);

        if(!parseOptional(lexer, this::parseRight)) {
            valid = false;
        }
        return true;
    }

    private boolean parseRight(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, INTEGER, REAL, VARIABLE)) {
            right = token;
            addChild(right);
            return true;
        }
        return false;
    }
}
