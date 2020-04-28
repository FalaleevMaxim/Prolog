package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;

public class CompareNode extends AbstractNode {
    private ExprOrValueNode left;
    private Token compareSign;
    private ExprOrValueNode right;

    public CompareNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        left = null;
        compareSign = null;
        right = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        ExprOrValueNode value = new ExprOrValueNode(this);
        if(!value.parse(lexer)) return false;
        left = value;
        addChild(value);

        Token token = lexer.nextNonIgnored();
        if(!ofType(token,
                TokenType.EQUALS,
                TokenType.GREATER,
                TokenType.GREATER_EQUALS,
                TokenType.LESSER,
                TokenType.LESSER_EQUALS,
                TokenType.NOT_EQUALS)) {
            return false;
        }
        compareSign = token;
        addChild(compareSign);

        if(!parseOptional(lexer, this::parseRight)) {
            valid = false;
        }
        return true;
    }

    private boolean parseRight(Lexer lexer) {
        ExprOrValueNode value = new ExprOrValueNode(this);
        if(value.parse(lexer)) {
            right = value;
            addChild(right);
            return true;
        }
        return false;
    }

    public ExprOrValueNode getLeft() {
        return left;
    }

    public Token getCompareSign() {
        return compareSign;
    }

    public ExprOrValueNode getRight() {
        return right;
    }
}
