package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

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
    protected ParsingResult parseInternal(Lexer lexer) {
        ParsingResult result = parseOptional(lexer, this::parseBinaryExpr);
        if(result.isOk()) return result;
        return parseOptional(lexer, this::parseValue);
    }

    private ParsingResult parseBinaryExpr(Lexer lexer) {
        BinaryMathExprNode binaryMathExprNode = new BinaryMathExprNode(this);
        if(binaryMathExprNode.parse(lexer).isOk()) {
            binaryExpr = binaryMathExprNode;
            addChild(binaryExpr);
            return ParsingResult.ok();
        }
        return ParsingResult.fail();
    }

    private ParsingResult parseValue(Lexer lexer) {
        ValueNode valueNode = new ValueNode(this);
        if(valueNode.parse(lexer).isOk()) {
            value = valueNode;
            addChild(value);
            return ParsingResult.ok();
        }
        return ParsingResult.fail();
    }

    public BinaryMathExprNode getBinaryExpr() {
        return binaryExpr;
    }

    public ValueNode getValue() {
        return value;
    }
}
