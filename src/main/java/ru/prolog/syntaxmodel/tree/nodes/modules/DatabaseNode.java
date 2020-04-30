package ru.prolog.syntaxmodel.tree.nodes.modules;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;
import ru.prolog.syntaxmodel.tree.nodes.FunctorDefNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

public class DatabaseNode extends AbstractNode {
    /**
     * Ключевое слово "database"
     */
    private Token databaseKeyword;

    /**
     * Дефис перед именем БД
     */
    private Token dash;

    /**
     * Имя БД
     */
    private Token name;

    private final List<FunctorDefNode> predicates = new ArrayList<>();

    public DatabaseNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        databaseKeyword = null;
        predicates.clear();
        dash = null;
        name = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(token.getTokenType() != TokenType.DATABASE_KEYWORD) return FAIL;
        databaseKeyword = token;
        addChild(token);

        while (parseOptional(lexer, this::parsePredicate).isOk());
        if(predicates.isEmpty()) {
            addError(databaseKeyword, true, "No predicates in database module");
        }
        return OK;
    }

    private boolean parseDatabaseName(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, TokenType.MINUS)) return false;
        dash = token;
        addChild(dash);

        if(!parseOptional(lexer, this::parseName).isOk()) {
            addError(dash, true, "Expected database name after '-'");
        }
        return true;
    }

    private ParsingResult parseName(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, TokenType.SYMBOL)) return OK;
        name = token;
        addChild(name);
        return FAIL;
    }

    private ParsingResult parsePredicate(Lexer lexer) {
        FunctorDefNode predicate = new FunctorDefNode(this);
        if(predicate.parse(lexer).isOk()) {
            predicates.add(predicate);
            addChild(predicate);
            return OK;
        }
        return FAIL;
    }

    public Token getDatabaseKeyword() {
        return databaseKeyword;
    }

    public Token getDash() {
        return dash;
    }

    public Token getName() {
        return name;
    }

    public List<FunctorDefNode> getPredicates() {
        return Collections.unmodifiableList(predicates);
    }
}
