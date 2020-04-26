package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompoundTypeNode extends AbstractNode implements Separated {
    /**
     * Точки с запятой, разделяющие функторы
     */
    private List<Token> semicolons = new ArrayList<>();

    /**
     * Список функторов
     */
    private List<FunctorDefNode> functors = new ArrayList<>();

    public CompoundTypeNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        semicolons.clear();
        functors.clear();
    }

    @Override
    protected boolean parseInternal(Lexer lexer) {
        FunctorDefNode functor = new FunctorDefNode(this);
        if (functor.parse(lexer)) {
            functors.add(functor);
            addChild(functor);
        } else {
            return false;
        }
        while (parseOptional(lexer, this::parseSemicolonAndFunctor)) ;
        return true;
    }

    private boolean parseSemicolonAndFunctor(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (token.getTokenType() == TokenType.SEMICOLON) {
            semicolons.add(token);
            addChild(token);
        } else {
            return false;
        }

        FunctorDefNode functor = new FunctorDefNode(this);
        if (functor.parse(lexer)) {
            functors.add(functor);
            addChild(functor);
        } else {
            valid = false;
        }
        return true;
    }

    public List<Token> getSemicolons() {
        return Collections.unmodifiableList(semicolons);
    }

    public List<FunctorDefNode> getFunctors() {
        return Collections.unmodifiableList(functors);
    }

    @Override
    public List<Token> getSeparators() {
        return getSemicolons();
    }
}
