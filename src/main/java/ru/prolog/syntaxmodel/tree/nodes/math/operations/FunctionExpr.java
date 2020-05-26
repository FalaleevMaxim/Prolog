package ru.prolog.syntaxmodel.tree.nodes.math.operations;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Bracketed;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

/**
 * Вызов математической функции (синус, косинус, модуль, логарифм и т.п.)
 */
public class FunctionExpr extends AbstractNode implements Bracketed {
    /**
     * Имя функции
     */
    Token name;

    /**
     * Открывающая скобка
     */
    private Token lb;

    /**
     * Выражение в скобках
     */
    private Expr expr;

    /**
     * Закрывающая скобка
     */
    private Token rb;

    public FunctionExpr(Token name, Token lb, Expr expr, Token rb) {
        super(null);
        this.name = name;
        this.lb = lb;
        this.expr = expr;
        this.rb = rb;
    }

    public FunctionExpr() {
        super(null);
    }

    public Token getName() {
        return name;
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

    public void setName(Token name) {
        this.name = name;
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
        name = null;
        lb = null;
        expr = null;
        rb = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        if (name == null) return ParsingResult.FAIL;
        addChild(name);

        if (lb == null) {
            addError(name, true, "Expected '('");
            return ParsingResult.OK;
        } else {
            addChild(lb);
        }

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

        if (rb == null) {
            addError(expr, true, "Expected ')'");
        } else {
            addChild(rb);
        }

        return ParsingResult.OK;
    }
}
