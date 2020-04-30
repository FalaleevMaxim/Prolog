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
    protected ParsingResult parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(token.getTokenType() != TokenType.PREDICATES_KEYWORD) return ParsingResult.fail();
        predicatesKeyword = token;
        addChild(token);

        while (parseOptional(lexer, this::parsePredicate).isOk());
        if(predicates.isEmpty()) {
            addError(predicatesKeyword, true, "No predicates in predicates module");
        }
        return ParsingResult.ok();
    }

    private ParsingResult parsePredicate(Lexer lexer) {
        FunctorDefNode predicate = new FunctorDefNode(this);
        if(predicate.parse(lexer).isOk()) {
            predicates.add(predicate);
            addChild(predicate);
            return ParsingResult.ok();
        }
        return ParsingResult.fail();
    }

    public Token getPredicatesKeyword() {
        return predicatesKeyword;
    }

    public List<FunctorDefNode> getPredicates() {
        return Collections.unmodifiableList(predicates);
    }
}
