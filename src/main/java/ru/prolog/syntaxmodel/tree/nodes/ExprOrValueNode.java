package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;

public class ExprOrValueNode extends AbstractNode {
    BinaryMathExprNode binaryExpr;
    ValueNode value;

    public ExprOrValueNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        value = null;
        binaryExpr = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        return parseOptional(lexer, this::parseBinaryExpr) || parseOptional(lexer, this::parseValue);
    }

    private boolean parseBinaryExpr(Lexer lexer) {
        BinaryMathExprNode binaryMathExprNode = new BinaryMathExprNode(this);
        if(binaryMathExprNode.parse(lexer)) {
            binaryExpr = binaryMathExprNode;
            addChild(binaryExpr);
            return true;
        }
        return false;
    }

    private boolean parseValue(Lexer lexer) {
        ValueNode valueNode = new ValueNode(this);
        if(valueNode.parse(lexer)) {
            value = valueNode;
            addChild(value);
            return true;
        }
        return false;
    }

    public BinaryMathExprNode getBinaryExpr() {
        return binaryExpr;
    }

    public ValueNode getValue() {
        return value;
    }
}
