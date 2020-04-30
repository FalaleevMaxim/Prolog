package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

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
    protected ParsingResult parseInternal(Lexer lexer) {
        ParsingResult result = parseOptional(lexer, this::parseCutSign);
        if(result.isOk()) return result;
        result = parseOptional(lexer, this::parsePredicateExec);
        if(result.isOk()) return result;
        return parseOptional(lexer, this::parseCompareStatement);
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

    private ParsingResult parseCutSign(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.CUT_SIGN)) {
            cutSign = token;
            addChild(token);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parsePredicateExec(Lexer lexer) {
        FunctorNode predExec = new FunctorNode(this);
        if (predExec.parse(lexer).isOk()) {
            predicateExec = predExec;
            addChild(predExec);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseCompareStatement(Lexer lexer) {
        CompareNode compareNode = new CompareNode(this);
        if (compareNode.parse(lexer).isOk()) {
            compareStatement = compareNode;
            addChild(compareNode);
            return OK;
        }
        return FAIL;
    }

    public CompareNode getCompareStatement() {
        return compareStatement;
    }
}
