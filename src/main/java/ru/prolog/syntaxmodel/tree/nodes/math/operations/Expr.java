package ru.prolog.syntaxmodel.tree.nodes.math.operations;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Обёртка над разными видами выражений
 */
public class Expr extends AbstractNode {
    /**
     * Число
     */
    private Token number;

    /**
     * Выражение с унарным оператором
     */
    private UnaryExprNode unaryExpr;

    /**
     * Выражение с бинарным оператором
     */
    private BinaryExprNode binaryExpr;
    /**
     * Выражение в скобках
     */
    private BracketedExpr bracketedExpr;
    /**
     * Вызов функции
     */
    private FunctionExpr functionExpr;

    public Expr(Token number) {
        super(null);
        this.number = number;
    }

    public Expr(UnaryExprNode unaryExpr) {
        super(null);
        this.unaryExpr = unaryExpr;
    }

    public Expr(BinaryExprNode binaryExpr) {
        super(null);
        this.binaryExpr = binaryExpr;
    }

    public Expr(BracketedExpr bracketedExpr) {
        super(null);
        this.bracketedExpr = bracketedExpr;
    }

    public Expr(FunctionExpr functionExpr) {
        super(null);
        this.functionExpr = functionExpr;
    }

    public Expr() {
        super(null);
    }

    public boolean isNumber() {
        return number != null;
    }

    public boolean isUnaryExpr() {
        return unaryExpr != null;
    }

    public boolean isBinaryExpr() {
        return binaryExpr != null;
    }

    public boolean isBracketedExpr() {
        return bracketedExpr != null;
    }

    public boolean isFunctionExpr() {
        return functionExpr != null;
    }

    public Token getNumber() {
        return number;
    }

    public void setNumber(Token number) {
        this.number = number;
    }

    public UnaryExprNode getUnaryExpr() {
        return unaryExpr;
    }

    public BinaryExprNode getBinaryExpr() {
        return binaryExpr;
    }

    public void setBinaryExpr(BinaryExprNode binaryExpr) {
        this.binaryExpr = binaryExpr;
    }

    public BracketedExpr getBracketedExpr() {
        return bracketedExpr;
    }

    public void setBracketedExpr(BracketedExpr bracketedExpr) {
        this.bracketedExpr = bracketedExpr;
    }

    public FunctionExpr getFunctionExpr() {
        return functionExpr;
    }

    public void setFunctionExpr(FunctionExpr functionExpr) {
        this.functionExpr = functionExpr;
    }

    @Override
    protected void clearInternal() {
        number = null;
        unaryExpr = null;
        binaryExpr = null;
        bracketedExpr = null;
        functionExpr = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        if (isNumber()) {
            addChild(number);
            return ParsingResult.OK;
        }
        if (isUnaryExpr()) return parseAndAdd(unaryExpr, lexer);
        if (isBinaryExpr()) return parseAndAdd(binaryExpr, lexer);
        if (isBracketedExpr()) return parseAndAdd(bracketedExpr, lexer);
        if (isFunctionExpr()) return parseAndAdd(functionExpr, lexer);
        return ParsingResult.FAIL;
    }

    private ParsingResult parseAndAdd(AbstractNode node, Lexer lexer) {
        node.setParent(this);
        ParsingResult result = node.parse(lexer);
        if (result.isOk()) addChild(node);
        return result;
    }

    public Collection<Token> getAllVariables() {
        if (isFunctionExpr()) return functionExpr.getExpr().getAllVariables();
        if (isBracketedExpr()) return bracketedExpr.getExpr().getAllVariables();
        if (isUnaryExpr()) return unaryExpr.getExpr().getAllVariables();
        if (isBinaryExpr()) {
            List<Token> variables = new ArrayList<>(binaryExpr.getLeft().getAllVariables());
            if(binaryExpr.getRight() != null) variables.addAll(binaryExpr.getRight().getAllVariables());
            return variables;
        }

        if (ofType(number, TokenType.VARIABLE)) {
            return Collections.singletonList(number);
        } else {
            return Collections.emptyList();
        }
    }
}
