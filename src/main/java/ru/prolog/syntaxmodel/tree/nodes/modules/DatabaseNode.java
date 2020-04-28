package ru.prolog.syntaxmodel.tree.nodes.modules;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.nodes.FunctorDefNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(token.getTokenType() != TokenType.DATABASE_KEYWORD) return false;
        databaseKeyword = token;
        addChild(token);

        while (parseOptional(lexer, this::parsePredicate));
        if(predicates.isEmpty()) {
            valid = false;
        }
        return true;
    }

    private boolean parseDatabaseName(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, TokenType.MINUS)) return false;
        dash = token;
        addChild(dash);

        if(!parseOptional(lexer, this::parseName)) {
            valid = false;
        }
        return true;
    }

    private boolean parseName(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, TokenType.SYMBOL)) return false;
        name = token;
        addChild(name);
        return true;
    }

    private boolean parsePredicate(Lexer lexer) {
        FunctorDefNode predicate = new FunctorDefNode(this);
        if(predicate.parse(lexer)) {
            predicates.add(predicate);
            addChild(predicate);
            return true;
        }
        return false;
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
