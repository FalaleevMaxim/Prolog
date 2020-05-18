package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.interfaces.Bracketed;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import static ru.prolog.syntaxmodel.TokenType.RB;
import static ru.prolog.syntaxmodel.tree.misc.ParsingResult.*;

public class NotPredicateExec extends AbstractNode implements Bracketed {
    private Token notWord;
    private Token lb;
    private Token rb;
    private FunctorNode predicateExec;

    public NotPredicateExec(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        notWord = null;
        lb = null;
        rb = null;
        predicateExec = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, TokenType.SYMBOL) || !"not".equals(token.getText())) return FAIL;
        notWord = token;
        addChild(token);
        return parseOptional(lexer, this::parseBrackets);
    }

    private ParsingResult parseBrackets(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if(!ofType(token, TokenType.LB)) {
            addError(notWord, true, "Expected '('");
            return FAIL;
        }
        lb = token;
        addChild(token);
        if(!parseOptional(lexer, this::parsePredicateExec).isOk()) {
            addError(lb, true, "Expected predicate call");
        }
        if(!parseOptional(lexer, this::parseClosing).isOk()) {
            addError(children().get(children().size() - 1), true, "Expected ')'");
        }
        return OK;
    }

    private ParsingResult parsePredicateExec(Lexer lexer) {
        return parseChildNode(new FunctorNode(this), lexer, p->predicateExec = p);
    }

    private ParsingResult parseClosing(Lexer lexer) {
        Token token = lexer.nextNonIgnored();
        if (ofType(token, RB)) {
            rb = token;
            addChild(token);
            return OK;
        }
        return FAIL;
    }

    public Token getNotWord() {
        return notWord;
    }

    @Override
    public Token getLb() {
        return lb;
    }

    @Override
    public Token getRb() {
        return rb;
    }

    public FunctorNode getPredicateExec() {
        return predicateExec;
    }
}
