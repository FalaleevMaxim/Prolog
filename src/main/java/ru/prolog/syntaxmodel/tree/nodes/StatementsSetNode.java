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

/**
 * Набор выражений в цели или правой части правила, разделённых запятой или "and".
 * В составе правила может быть несколько таких наборов, разделённых
 */
public class StatementsSetNode extends AbstractNode implements Separated {
    public static final Set<TokenType> FOLLOW_SET = Collections.unmodifiableSet(EnumSet.of(COMMA, AND_KEYWORD));

    private final List<Token> andSigns = new ArrayList<>();
    private final List<StatementNode> statements = new ArrayList<>();

    public StatementsSetNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        andSigns.clear();
        statements.clear();
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) { //ToDo использовать follow-set
        if (!parseOptional(lexer, this::parseStatement).isOk()) return FAIL;
        while (parseOptional(lexer, this::parseAndAndStatement).isOk());
        return OK;
    }

    public List<Token> getAndSigns() {
        return Collections.unmodifiableList(andSigns);
    }

    public List<StatementNode> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    @Override
    public List<Token> getSeparators() {
        return getAndSigns();
    }

    private ParsingResult parseStatement(Lexer lexer) {
        StatementNode statement = new StatementNode(this);
        ParsingResult result = statement.parse(lexer);
        if(result.isOk()) {
            statements.add(statement);
            addChild(statement);
        }
        return result;
    }

    private ParsingResult parseAndAndStatement(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (!ofType(token, TokenType.COMMA, TokenType.AND_KEYWORD)) {
            return FAIL;
        }
        andSigns.add(token);
        addChild(token);

        ParsingResult result = parseOptional(lexer, this::parseStatement);
        if(!result.isOk()) {
            addError(token, true, "Expected statement");
        }
        return result;
    }

    @Override
    protected Set<TokenType> initialFollowSet() {
        return FOLLOW_SET;
    }
}
