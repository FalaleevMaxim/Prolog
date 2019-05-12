package ru.prolog.syntaxmodel.tree;

import ru.prolog.syntaxmodel.TokenKind;
import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.source.CodeSource;

import java.util.Collections;
import java.util.List;

public class Token implements Node {
    private TokenType type;
    private final List<Token> tokens = Collections.singletonList(this);
    private String text;
    private CodeSource source;
    private AbstractNode parent;
    private int lineBreaks;

    public Token(TokenType type, String text, CodeSource source, AbstractNode parent) {
        this.type = type;
        this.text = text;
        this.source = source;
        this.parent = parent;
        this.lineBreaks = (int) text.chars().filter(c -> c == '\n').count();
    }

    public TokenKind getTokenKind() {
        return type.getTokenKind();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public List<Node> children() {
        return Collections.emptyList();
    }

    @Override
    public List<Token> tokens() {
        return tokens;
    }

    @Override
    public List<Token> tokens(TokenKind type) {
        return getTokenKind() == type ? tokens : Collections.emptyList();
    }

    @Override
    public List<Token> meaningTokens() {
        return getTokenKind().isMeaning() ? tokens : Collections.emptyList();
    }

    @Override
    public AbstractNode parent() {
        return parent;
    }

    @Override
    public void setParent(AbstractNode parent) {
        this.parent = parent;
    }

    @Override
    public int startPos() {
        if (parent == null) return 0;
        return parent.startPos(this);
    }

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public int line() {
        if (parent == null) return 0;
        return parent.linesBefore(this);
    }

    @Override
    public int lineBreaks() {
        return lineBreaks;
    }

    @Override
    public Token tokenByRelativePos(int relativePos) {
        checkPos(relativePos);
        return this;
    }

    @Override
    public Node minNodeIncludes(int relativeStartPos, int relativeEndPos) {
        checkPos(relativeStartPos);
        checkPos(relativeEndPos);
        return this;
    }

    private void checkPos(int relativeStartPos) {
        if (relativeStartPos < 0 || relativeStartPos >= length())
            throw new StringIndexOutOfBoundsException(relativeStartPos);
    }
}
