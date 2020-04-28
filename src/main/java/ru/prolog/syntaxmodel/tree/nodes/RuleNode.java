package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleNode extends AbstractNode implements Separated {
    /**
     * Левая сторона правила
     */
    private FunctorNode left;

    /**
     * Знак :- или ключевое слово if
     */
    private Token ifSign;

    /**
     * Наборы выражений, разделённые символом ; или ключевым словом or
     */
    private final List<StatementsSetNode> statementsSets = new ArrayList<>();

    /**
     * Символы ; или ключевые слова or, разделяющие наборы выражений
     */
    private final List<Token> orSigns = new ArrayList<>();

    /**
     * Точка в конце правила
     */
    private Token dot;

    public RuleNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        left = null;
        ifSign = null;
        statementsSets.clear();
        orSigns.clear();
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        FunctorNode left = new FunctorNode(this);
        if (!left.parse(lexer)) return false;
        this.left = left;
        addChild(left);

        if(parseOptional(lexer, this::parseDot)) return true;
        if(!parseOptional(lexer, this::parseIf)) {
            valid = false;
            return true;
        }

        if (!parseOptional(lexer, this::parseStatementsSet)) {
            valid = false;
            return true;
        }

        while (parseOptional(lexer, this::parseOrAndStatementSet));
        if(!parseOptional(lexer, this::parseDot)) {
            valid = false;
        }
        return true;
    }

    public FunctorNode getLeft() {
        return left;
    }

    public Token getIfSign() {
        return ifSign;
    }

    public List<StatementsSetNode> getStatementsSets() {
        return Collections.unmodifiableList(statementsSets);
    }

    public List<Token> getOrSigns() {
        return Collections.unmodifiableList(orSigns);
    }

    @Override
    public List<Token> getSeparators() {
        return getOrSigns();
    }

    public Token getDot() {
        return dot;
    }

    private boolean parseDot(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.DOT)) {
            dot = token;
            addChild(dot);
            return true;
        }
        return false;
    }

    private boolean parseIf(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.IF_SIGN, TokenType.IF_KEYWORD)) {
            ifSign = token;
            addChild(ifSign);
            return true;
        }
        return false;
    }

    private boolean parseStatementsSet(Lexer lexer) {
        StatementsSetNode statementsSet = new StatementsSetNode(this);
        if(statementsSet.parse(lexer)) {
            statementsSets.add(statementsSet);
            addChild(statementsSet);
            return true;
        }
        return false;
    }

    private boolean parseOrAndStatementSet(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (!ofType(token, TokenType.SEMICOLON, TokenType.OR_KEYWORD)) {
            return false;
        }
        orSigns.add(token);
        addChild(token);

        if (!parseOptional(lexer, this::parseStatementsSet)) {
            valid = false;
        }
        return true;
    }
}
