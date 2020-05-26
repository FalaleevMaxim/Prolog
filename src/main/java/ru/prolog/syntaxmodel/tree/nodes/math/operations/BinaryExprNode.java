package ru.prolog.syntaxmodel.tree.nodes.math.operations;

import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

/**
 * Оператор с двумя аргументами
 */
public class BinaryExprNode extends AbstractNode {
    /**
     * Левый операнд
     */
    private Expr left;

    /**
     * Оператор + - * / d m
     */
    private Token operator;
    /**
     * Правый операнд
     */
    private Expr right;

    public BinaryExprNode(Expr left, Token operator, Expr right) {
        super(null);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public BinaryExprNode() {
        super(null);
    }

    public Expr getLeft() {
        return left;
    }

    public void setLeft(Expr left) {
        this.left = left;
    }

    public Token getOperator() {
        return operator;
    }

    public void setOperator(Token operator) {
        this.operator = operator;
    }

    public Expr getRight() {
        return right;
    }

    public void setRight(Expr right) {
        this.right = right;
    }

    @Override
    protected void clearInternal() {
        left = null;
        right = null;
        operator = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        left.setParent(this);
        ParsingResult result = left.parse(lexer);
        if (!result.isOk()) return result;
        addChild(left);
        if (operator == null) return ParsingResult.FAIL;
        addChild(operator);

        result = ParsingResult.FAIL;
        if (right != null) {
            right.setParent(this);
            result = right.parse(lexer);
        }
        if (result.isOk()) {
            addChild(right);
        } else {
            addError(operator, true, "Expected second operand");
        }
        return ParsingResult.OK;
    }
}
