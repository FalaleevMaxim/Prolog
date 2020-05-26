package ru.prolog.syntaxmodel.tree.nodes.math.operations;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Bracketed;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

/**
 * Выражение в скобках
 */
public class BracketedExpr extends AbstractNode implements Bracketed {
    /**
     * Открывающая скобка
     */
    private Token lb;

    /**
     * Выражение
     */
    private Expr expr;

    /**
     * Закрывающая скобка
     */
    private Token rb;

    public BracketedExpr(Token lb, Expr expr, Token rb) {
        super(null);
        this.lb = lb;
        this.expr = expr;
        this.rb = rb;
    }

    public BracketedExpr() {
        super(null);
    }

    @Override
    public Token getLb() {
        return lb;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public Token getRb() {
        return rb;
    }

    public void setLb(Token lb) {
        this.lb = lb;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    public void setRb(Token rb) {
        this.rb = rb;
    }

    @Override
    protected void clearInternal() {
        lb = null;
        expr = null;
        rb = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        if (lb == null) return ParsingResult.FAIL;
        addChild(lb);

        ParsingResult result = ParsingResult.FAIL;
        if (expr != null) {
            expr.setParent(this);
            result = expr.parse(lexer);
        }
        if (result.isOk()) {
            addChild(expr);
        } else {
            addError(lb, true, "Expected value or expression");
            return ParsingResult.OK;
        }
        if (rb != null) {
            addChild(rb);
        } else {
            addError(expr, false, "No closing bracket");
        }
        return ParsingResult.OK;
    }
}
