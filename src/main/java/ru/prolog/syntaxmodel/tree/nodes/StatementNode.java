package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;

/**
 * Выражение в составе цели или правой стороны правила.
 * Может быть вызовом предиката, выражением сравнения или отсечением
 */
public class StatementNode extends AbstractNode {
    /**
     * Отсечение в виде символа '!'
     */
    private Token cutSign;

    /**
     * Вызов предиката
     */
    private FunctorNode predicateExec;

    /**
     * Выражение сравнения
     */
    private CompareNode compareStatement;

    public StatementNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        cutSign = null;
        predicateExec = null;
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        return parseOptional(lexer, this::parseCutSign)
                || parseOptional(lexer, this::parsePredicateExec)
                || parseOptional(lexer, this::parseCompareStatement);
    }

    public boolean isCutSign() {
        return cutSign != null;
    }

    public boolean isPredicateExec() {
        return predicateExec != null;
    }

    public boolean isCompareStatement() {
        return compareStatement != null;
    }

    public Token getCutSign() {
        return cutSign;
    }

    public FunctorNode getPredicateExec() {
        return predicateExec;
    }

    private boolean parseCutSign(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.CUT_SIGN)) {
            cutSign = token;
            addChild(token);
            return true;
        }
        return false;
    }

    private boolean parsePredicateExec(Lexer lexer) {
        FunctorNode predExec = new FunctorNode(this);
        if (predExec.parse(lexer)) {
            predicateExec = predExec;
            addChild(predExec);
            return true;
        }
        return false;
    }

    private boolean parseCompareStatement(Lexer lexer) {
        CompareNode compareNode = new CompareNode(this);
        if (compareNode.parse(lexer)) {
            compareStatement = compareNode;
            addChild(compareNode);
            return true;
        }
        return false;
    }

    public CompareNode getCompareStatement() {
        return compareStatement;
    }
}
