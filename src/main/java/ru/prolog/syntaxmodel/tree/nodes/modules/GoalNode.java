package ru.prolog.syntaxmodel.tree.nodes.modules;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;
import ru.prolog.syntaxmodel.tree.nodes.StatementsSetNode;

import java.util.*;

public class GoalNode extends AbstractNode {
    public static final Set<TokenType> FOLLOW_SET = Collections.unmodifiableSet(EnumSet.of(
            TokenType.OR_KEYWORD,
            TokenType.SEMICOLON,
            TokenType.DOT));

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
    protected ParsingResult parseInternal(Lexer lexer) { //ToDo использовать follow-set
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, TokenType.GOAL_KEYWORD)) return ParsingResult.fail();
        goalKeyword = token;
        addChild(token);

        if (!parseOptional(lexer, this::parseStatementsSet).isOk()) {
            addError(goalKeyword, true, "Expected goal");
            return ParsingResult.ok();
        }

        while (parseOptional(lexer, this::parseOrAndStatementSet).isOk());
        parseOptional(lexer, this::parseDot);
        return ParsingResult.ok();
    }

    private ParsingResult parseStatementsSet(Lexer lexer) {
        StatementsSetNode statementsSet = new StatementsSetNode(this);
        if(statementsSet.parse(lexer).isOk()) {
            statementsSets.add(statementsSet);
            addChild(statementsSet);
            return ParsingResult.ok();
        }
        return ParsingResult.fail();
    }

    private ParsingResult parseOrAndStatementSet(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (!ofType(token, TokenType.SEMICOLON, TokenType.OR_KEYWORD)) {
            return ParsingResult.fail();
        }
        orSigns.add(token);
        addChild(token);

        if (!parseOptional(lexer, this::parseStatementsSet).isOk()) {
            addError(token, true, "Expected statement");
        }
        return ParsingResult.ok();
    }

    private ParsingResult parseDot(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.DOT)) {
            dot = token;
            addChild(dot);
            return ParsingResult.ok();
        }
        return ParsingResult.ok();
    }

    @Override
    protected Set<TokenType> initialFollowSet() {
        return FOLLOW_SET;
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
