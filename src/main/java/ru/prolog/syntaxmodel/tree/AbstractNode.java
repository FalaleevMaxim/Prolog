package ru.prolog.syntaxmodel.tree;

import ru.prolog.syntaxmodel.TokenKind;
import ru.prolog.syntaxmodel.source.CodeSource;
import ru.prolog.syntaxmodel.util.MappingCharSequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public abstract class AbstractNode implements Node {
    private List<Node> children = new ArrayList<>();
    private List<Node> unmodifiebleChildren = Collections.unmodifiableList(children);
    private AbstractNode parent;
    private CodeSource source;

    /**
     * Закэшированная длина элемента в символах.
     * Длину можно было бы получать, суммируя длины токенов, но это неоптимально.
     * При обновлении длины токена или элемента он должен оповестить родителя об этом изменении чтобы тот обновил свою длину.
     *
     * @see #updateLength()
     * @see #updateLength(int)
     */
    private int cachedLength;
    /**
     * Аналогично с {@link #cachedLength}, закэшированное количество переносов строк внутри
     *
     * @see #updateLineBreaks()
     * @see #updateLineBreaks(int)
     */
    private int cachedLineBreaks;

    protected AbstractNode(CodeSource source) {
        this.source = source;
    }

    @Override
    public List<Node> children() {
        return unmodifiebleChildren;
    }

    @Override
    public List<Token> tokens() {
        return children.stream()
                .map(Node::tokens)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<Token> tokens(TokenKind type) {
        return children.stream()
                .map(Node::tokens)
                .flatMap(Collection::stream)
                .filter(token -> token.getTokenKind().equals(type))
                .collect(Collectors.toList());
    }

    @Override
    public List<Token> meaningTokens() {
        return children.stream()
                .map(Node::tokens)
                .flatMap(Collection::stream)
                .filter(token -> token.getTokenKind().isMeaning())
                .collect(Collectors.toList());
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
    public int length() {
        return cachedLength;
    }

    public void insertBefore(Node newChild, Node before) {
        int index = children.indexOf(before);
        if (index < 0) return;
        insertChild(newChild, index);
    }

    public void insertBefore(List<? extends Node> newChildren, Node before) {
        int index = children.indexOf(before);
        if (index < 0) return;
        children.addAll(index, newChildren);
        updateLength(sumLength(newChildren));
    }

    public void insertAfter(Node newChild, Node after) {
        int index = children.indexOf(after);
        if (index < 0) return;
        insertChild(newChild, index + 1);
    }

    public void insertAfter(List<? extends Node> newChildren, Node after) {
        int index = children.indexOf(after);
        if (index < 0) return;
        children.addAll(index + 1, newChildren);
        updateLength(sumLength(newChildren));
    }

    public void addChild(Node child) {
        insertChild(child, children.size());
    }

    public void addChildren(List<? extends Node> newChildren) {
        children.addAll(newChildren);
        updateLength(sumLength(newChildren));
    }

    public void insertChild(Node child, int index) {
        children.add(index, child);
        updateLength(child.length());
    }

    @Override
    public int line() {
        if (parent == null) return 0;
        return parent.linesBefore(this);
    }

    /**
     * Подсчитывает, сколько переносов строк было перед началом переданного элемента.
     *
     * @param child Элемент, для которого требуется найти строку начала.
     * @return Номер строки на которой начинается элемент.
     */
    public int linesBefore(Node child) {
        return countUntilFoundChild(child,
                parent::linesBefore,
                Node::lineBreaks);
    }

    /**
     * Обновляет сохранённую длину при изменении длины составляющих элементов
     */
    public void updateLength() {
        int newSize = children.stream().mapToInt(Node::length).sum();
        if (newSize == cachedLength) return;
        int change = newSize - cachedLength;
        cachedLength = newSize;
        if (parent != null) {
            parent.updateLength(change);
        }
    }

    private void updateLength(int change) {
        cachedLength += change;
        if (parent != null) {
            parent.updateLength(change);
        }
    }

    private int sumLength(Collection<? extends Node> nodes) {
        return nodes.stream().mapToInt(Node::length).sum();
    }

    @Override
    public int lineBreaks() {
        return cachedLineBreaks;
    }

    public void updateLineBreaks() {
        int newSize = children.stream().mapToInt(Node::lineBreaks).sum();
        if (newSize == cachedLineBreaks) return;
        int change = newSize - cachedLineBreaks;
        cachedLineBreaks = newSize;
        if (parent != null) {
            parent.updateLineBreaks(change);
        }
    }

    private void updateLineBreaks(int change) {
        cachedLineBreaks += change;
        if (parent != null) {
            parent.updateLineBreaks(change);
        }
    }

    public void update() {
        updateLength();
        updateLineBreaks();
    }

    @Override
    public int startPos() {
        if (parent == null) return 0;
        return parent.startPos(this);
    }

    public int startPos(Node child) {
        return countUntilFoundChild(child,
                parent::startPos,
                Node::length);
    }

    @Override
    public String getText() {
        int startPos = startPos();
        return source.getTreeSource().substring(startPos, startPos + length());
    }

    @Override
    public Token tokenByRelativePos(int relativePos) {
        checkPos(relativePos);
        int start;
        int end = 0;
        for (Node child : children()) {
            start = end;
            end += child.length();
            if (relativePos < end) return child.tokenByRelativePos(relativePos - start);
        }
        throw new IllegalStateException("This can never be thrown, if pos is between 0 and length(), there must be a token!");
    }

    @Override
    public Node minNodeIncludes(int relativeStartPos, int relativeEndPos) {
        checkPos(relativeStartPos);
        checkPos(relativeEndPos);
        int start;
        int end = 0;
        for (Node child : children()) {
            start = end;
            end += child.length();
            if (relativeStartPos >= start && relativeEndPos < end)
                return child.minNodeIncludes(relativeStartPos, relativeEndPos);
        }
        return this;
    }

    private void checkPos(int relativeStartPos) {
        if (relativeStartPos < 0 || relativeStartPos >= length())
            throw new StringIndexOutOfBoundsException(relativeStartPos);
    }

    /**
     * Общая логика подсчёта позиции и строки начала элемента.
     * Складывает значения (длины или количества переносов строк) у дочерних элементов пока не дойдёт до переданного элемента
     *
     * @param child          Элемент, позицию или строку начала которого нужно найти
     * @param initFromParent Инициализация счётчика вызовом функции у родительского элемента
     * @param whatToCount    Что подсчитывать у дочерних элементов (длину или количество переносов строк)
     * @return результат подсчёта.
     * @throws IllegalArgumentException Если {@param child} не является дочерним элементом.
     * @see #startPos(Node)
     * @see #linesBefore(Node)
     */
    private int countUntilFoundChild(Node child, ToIntFunction<Node> initFromParent, ToIntFunction<Node> whatToCount) {
        int counter = 0;
        boolean found = false;
        if (parent != null) counter = initFromParent.applyAsInt(this);
        for (Node node : children) {
            if (node == child) {
                found = true;
                break;
            }
            counter += whatToCount.applyAsInt(node);
        }
        if (!found) throw new IllegalArgumentException("Given node is not child!");
        return counter;
    }
}
