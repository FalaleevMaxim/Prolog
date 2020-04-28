package ru.prolog.syntaxmodel.tree;

import ru.prolog.syntaxmodel.TokenKind;
import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.tree.recognizers.Hint;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Токен
 */
public class Token implements Node {
    /**
     * Тип токена
     */
    private TokenType type;
    /**
     * Этот же токен в виде списка из одного элемента для метода {@link #tokens()}
     */
    private final List<Token> tokens = Collections.singletonList(this);
    /**
     * Текст токена
     */
    private String text;
    /**
     * Родительский узел
     */
    private AbstractNode parent;

    /**
     * Предыдущий токен
     */
    private Token prev;

    /**
     * Следующий токен
     */
    private Token next;
    /**
     * Количество переносов строк внутри токена
     */
    private int lineBreaks;
    /**
     * Распознан частично.
     * {@code true} означает, что имеющегося текста было достаточно, чтобы точно определить тип токена, но есть ошибки.
     */
    private boolean partial = false;
    /**
     * Подсказки, как можно дополнить.
     */
    private Hint hint;

    public Token(TokenType type, String text, AbstractNode parent, boolean isPartial) {
        this(type, text, parent);
        this.partial = isPartial;
    }

    public Token(TokenType type, String text, AbstractNode parent) {
        this.type = type;
        this.text = text;
        this.parent = parent;
        this.lineBreaks = (int) text.chars().filter(c -> c == '\n').count();
    }

    public TokenType getTokenType() {
        return type;
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
        return !partial;
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
    public Token firstToken() {
        return this;
    }

    @Override
    public Token lastToken() {
        return this;
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

    /**
     * Проверяет, что данный токен находится после переданного
     */
    public boolean isAfter(Token before) {
        if (prev == null) return false;
        if (prev == before) return true;
        return prev.isAfter(before);
    }

    /**
     * Проверяет, что данный токен находится перед переданным
     */
    public boolean isBefore(Token after) {
        if (next == null) return false;
        if (next == after) return true;
        return next.isBefore(after);
    }

    public List<Token> intervalTo(Token to) {
        List<Token> interval = new LinkedList<>();
        if (intervalTo(interval, to)) {
            return interval;
        }
        return null;
    }

    private boolean intervalTo(List<Token> acc, Token to) {
        if (this == to) {
            acc.add(this);
            return true;
        }
        if (next == null) {
            if (to == null) {
                acc.add(this);
                return true;
            }
            return false;
        }
        acc.add(this);
        return next.intervalTo(acc, to);
    }

    private void checkPos(int relativeStartPos) {
        if (relativeStartPos < 0 || relativeStartPos >= length())
            throw new StringIndexOutOfBoundsException(relativeStartPos);
    }

    public Token getPrev() {
        return prev;
    }

    public void setPrev(Token prev) {
        this.prev = prev;
    }

    public Token getNext() {
        return next;
    }

    public void setNext(Token next) {
        this.next = next;
    }

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    public void setHint(Hint hint) {
        this.hint = hint;
    }

    public Hint getHint() {
        return hint;
    }

    @Override
    public String toString() {
        return "<" + type +">" + text;
    }
}
