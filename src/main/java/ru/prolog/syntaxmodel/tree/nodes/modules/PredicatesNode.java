package ru.prolog.syntaxmodel.tree.nodes.modules;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.nodes.FunctorDefNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PredicatesNode extends AbstractNode {
    private Token predicatesKeyword;

    private final List<FunctorDefNode> predicates = new ArrayList<>();

    public PredicatesNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        predicatesKeyword = null;
        predicates.clear();
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(token.getTokenType() != TokenType.PREDICATES_KEYWORD) return false;
        predicatesKeyword = token;
        addChild(token);

        while (parseOptional(lexer, this::parsePredicate));
        if(predicates.isEmpty()) {
            valid = false;
        }
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

    public Token getPredicatesKeyword() {
        return predicatesKeyword;
    }

    public List<FunctorDefNode> getPredicates() {
        return Collections.unmodifiableList(predicates);
    }
}
