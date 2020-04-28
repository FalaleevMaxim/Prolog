package ru.prolog.syntaxmodel.tree.nodes.modules;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.nodes.StatementsSetNode;

import java.util.ArrayList;
import java.util.List;

public class GoalNode extends AbstractNode {
    /**
     * Ключевое слово goal
     */
    private Token goalKeyword;

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

    public GoalNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        goalKeyword = null;
        statementsSets.clear();
        orSigns.clear();
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, TokenType.GOAL_KEYWORD)) return false;
        goalKeyword = token;
        addChild(token);

        if (!parseOptional(lexer, this::parseStatementsSet)) {
            valid = false;
            return true;
        }

        while (parseOptional(lexer, this::parseOrAndStatementSet));
        parseOptional(lexer, this::parseDot);
        return true;
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

    private boolean parseDot(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.DOT)) {
            dot = token;
            addChild(dot);
            return true;
        }
        return false;
    }

    public Token getGoalKeyword() {
        return goalKeyword;
    }

    public List<StatementsSetNode> getStatementsSets() {
        return statementsSets;
    }

    public List<Token> getOrSigns() {
        return orSigns;
    }

    public Token getDot() {
        return dot;
    }
}
