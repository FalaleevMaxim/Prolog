package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Набор выражений в цели или правой части правила, разделённых запятой или "and".
 * В составе правила может быть несколько таких наборов, разделённых
 */
public class StatementsSetNode extends AbstractNode implements Separated {
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
    protected boolean parseInternal(Lexer lexer) {
        if (!parseOptional(lexer, this::parseStatement)) return false;
        while (parseOptional(lexer, this::parseAndAndStatement));
        return true;
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

    private boolean parseStatement(Lexer lexer) {
        StatementNode statement = new StatementNode(this);
        if(statement.parse(lexer)) {
            statements.add(statement);
            addChild(statement);
            return true;
        }
        return false;
    }

    private boolean parseAndAndStatement(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (!ofType(token, TokenType.COMMA, TokenType.AND_KEYWORD)) {
            return false;
        }
        andSigns.add(token);
        addChild(token);

        if(!parseOptional(lexer, this::parseStatement)) {
            valid = false;
        }
        return true;
    }
}
