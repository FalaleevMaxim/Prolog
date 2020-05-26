package ru.prolog.syntaxmodel.tree.nodes;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.AbstractNode;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompareNode extends AbstractNode {
    public static final Set<TokenType> FOLLOW_SET = Collections.unmodifiableSet(EnumSet.of(TokenType.EQUALS,
            TokenType.GREATER,
            TokenType.GREATER_EQUALS,
            TokenType.LESSER,
            TokenType.LESSER_EQUALS,
            TokenType.NOT_EQUALS));

    private ExprOrValueNode left;
    private Token compareSign;
    private ExprOrValueNode right;

    public CompareNode(AbstractNode parent) {
        super(parent);
    }

    @Override
    protected void clearInternal() {
        left = null;
        compareSign = null;
        right = null;
    }

    @Override
    protected ParsingResult parseInternal(Lexer lexer) { //ToDo использовать follow-set
        ExprOrValueNode value = new ExprOrValueNode(this);
        if(!value.parse(lexer).isOk()) return ParsingResult.fail();
        left = value;
        addChild(value);

        Token token = lexer.nextNonIgnored();
        if(!ofType(token,
                TokenType.EQUALS,
                TokenType.GREATER,
                TokenType.GREATER_EQUALS,
                TokenType.LESSER,
                TokenType.LESSER_EQUALS,
                TokenType.NOT_EQUALS)) {
            return ParsingResult.fail();
        }
        compareSign = token;
        addChild(compareSign);
        dontExpect();

        if(!parseOptional(lexer, this::parseRight).isOk()) {
            addError(compareSign, true, "Expected value or expression");
        }
        return ParsingResult.ok();
    }

    private ParsingResult parseRight(Lexer lexer) {
        ExprOrValueNode value = new ExprOrValueNode(this);
        if(value.parse(lexer).isOk()) {
            right = value;
            addChild(right);
            return ParsingResult.ok();
        }
        return ParsingResult.fail();
    }

    @Override
    protected Set<TokenType> initialFollowSet() {
        return FOLLOW_SET;
    }

    public ExprOrValueNode getLeft() {
        return left;
    }

    public Token getCompareSign() {
        return compareSign;
    }

    public ExprOrValueNode getRight() {
        return right;
    }

    public Collection<Token> getAllVariables(boolean includeAnonymous) {
        return Stream.of(left, right)
                .map(v->v.getAllVariables(includeAnonymous))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
