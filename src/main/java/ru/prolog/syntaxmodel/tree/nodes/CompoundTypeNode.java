package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Separated;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import java.util.*;

import static ru.prolog.syntaxmodel.TokenType.*;
import static ru.prolog.syntaxmodel.TokenType.MOD;
import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

public class CompoundTypeNode extends AbstractNode implements Separated {
    public static final Set<TokenType> FOLLOW_SET = Collections.singleton(SEMICOLON);

    /**
     * Точки с запятой, разделяющие функторы
     */
    private final List<Token> semicolons = new ArrayList<>();

    /**
     * Список функторов
     */
    private final List<FunctorDefNode> functors = new ArrayList<>();

    public CompoundTypeNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        semicolons.clear();
        functors.clear();
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) { //ToDo Использовать follow-set
        FunctorDefNode functor = new FunctorDefNode(this);
        if (functor.parse(lexer).isOk()) {
            functors.add(functor);
            addChild(functor);
        } else {
            return FAIL;
        }
        while (parseOptional(lexer, this::parseSemicolonAndFunctor).isOk()) ;
        return OK;
    }

    private ParsingResult parseSemicolonAndFunctor(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, TokenType.SEMICOLON)) {
            semicolons.add(token);
            addChild(token);
        } else {
            return FAIL;
        }

        FunctorDefNode functor = new FunctorDefNode(this);
        if (functor.parse(lexer).isOk()) {
            functors.add(functor);
            addChild(functor);
        } else {
            addError(token, true, "Expected functor");
        }
        return OK;
    }

    @Override
    protected Set<TokenType> initialFollowSet() {
        return FOLLOW_SET;
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
