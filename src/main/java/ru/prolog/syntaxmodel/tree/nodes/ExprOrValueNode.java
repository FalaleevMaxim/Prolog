package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;
import ru.prolog.syntaxmodel.tree.nodes.math.MathExpressionRootNode;

import java.util.Collection;

public class ExprOrValueNode extends AbstractNode {
    MathExpressionRootNode mathExpr;
    ValueNode value;

    public ExprOrValueNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        value = null;
        mathExpr = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        ParsingResult result = parseOptional(lexer, this::parseBinaryExpr);
        if(result.isOk()) return result;
        return parseOptional(lexer, this::parseValue);
    }

    private ParsingResult parseBinaryExpr(Lexer lexer) {
        MathExpressionRootNode mathExpr = new MathExpressionRootNode(this);
        if(mathExpr.parse(lexer).isOk()) {
            this.mathExpr = mathExpr;
            addChild(mathExpr);
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

    public boolean isValue() {
        return value != null;
    }

    public boolean isExpr() {
        return mathExpr != null;
    }

    public MathExpressionRootNode getBinaryExpr() {
        return mathExpr;
    }

    public ValueNode getValue() {
        return value;
    }

    public Collection<Token> getAllVariables(boolean includeAnonymous) {
        if(isValue()) return value.getAllVariables(includeAnonymous);
        return mathExpr.getAllVariables();
    }
}
