package ru.prolog.syntaxmodel.tree.nodes.math.operations;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

public class UnaryExprNode extends AbstractNode {
    private Token operator;
    private Expr expr;

    public UnaryExprNode(Token operator, Expr expr) {
        super(null);
        this.operator = operator;
        this.expr = expr;
    }

    public UnaryExprNode() {
        super(null);
    }

    public Token getOperator() {
        return operator;
    }

    public void setOperator(Token operator) {
        this.operator = operator;
    }

    public Expr getExpr() {
        return expr;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    @Override
    protected void clearInternal() {
        operator = null;
        expr = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        if (operator == null) return ParsingResult.FAIL;
        addChild(operator);

        ParsingResult result = ParsingResult.FAIL;
        if (expr != null) {
            expr.setParent(this);
            result = expr.parse(lexer);
        }
        if (result.isOk()) {
            addChild(expr);
        } else {
            addError(operator, true, "Expected operand");
        }
        return ParsingResult.OK;
    }
}
