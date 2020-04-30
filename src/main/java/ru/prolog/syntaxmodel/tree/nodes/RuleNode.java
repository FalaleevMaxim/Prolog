package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import java.util.*;

import static ru.prolog.syntaxmodel.TokenType.*;
import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

public class RuleNode extends AbstractNode implements Separated {
    public static final Set<TokenType> FOLLOW_SET = Collections.unmodifiableSet(
            EnumSet.of(IF_KEYWORD, IF_SIGN, OR_KEYWORD, SEMICOLON, DOT));

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
    protected ParsingResult parseInternal(Lexer lexer) { //ToDo использовать follow-set
        FunctorNode left = new FunctorNode(this);
        if (!left.parse(lexer).isOk()) return FAIL;
        this.left = left;
        addChild(left);

        if(parseOptional(lexer, this::parseDot).isOk()) return OK;
        if(!parseOptional(lexer, this::parseIf).isOk()) {
            addError(left, true, "Expected ':-', 'if' or '.'");
            return OK;
        }

        if (!parseOptional(lexer, this::parseStatementsSet).isOk()) {
            addError(ifSign, true, "Expected statement");
            return OK;
        }

        while (parseOptional(lexer, this::parseOrAndStatementSet).isOk());
        if(!parseOptional(lexer, this::parseDot).isOk()) {
            addError(statementsSets.get(statementsSets.size()-1), true, "Expected '.', ',' or ';'");
        }
        return OK;
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

    private ParsingResult parseDot(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.DOT)) {
            dot = token;
            addChild(dot);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseIf(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, TokenType.IF_SIGN, TokenType.IF_KEYWORD)) {
            ifSign = token;
            addChild(ifSign);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseStatementsSet(Lexer lexer) {
        StatementsSetNode statementsSet = new StatementsSetNode(this);
        if(statementsSet.parse(lexer).isOk()) {
            statementsSets.add(statementsSet);
            addChild(statementsSet);
            return OK;
        }
        return FAIL;
    }

    private ParsingResult parseOrAndStatementSet(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (!ofType(token, TokenType.SEMICOLON, TokenType.OR_KEYWORD)) {
            return FAIL;
        }
        orSigns.add(token);
        addChild(token);

        if (!parseOptional(lexer, this::parseStatementsSet).isOk()) {
            addError(token, true, "Expected statement");
        }
        return OK;
    }

    @Override
    protected Set<TokenType> initialFollowSet() {
        return FOLLOW_SET;
    }
}
