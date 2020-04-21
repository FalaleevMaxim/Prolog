package ru.prolog.syntaxmodel.tree;

import ru.prolog.syntaxmodel.TokenKind;
import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public abstract class AbstractNode implements Node {

    /**
     * Показывает, выполнился ли успешно последний parse этого объекта.
     */
    private volatile boolean initialized;

    /**
     * Хранит изменяемый список дочерних узлов.
     */
    private final List<Node> children = new ArrayList<>();
    /**
     * Обёртка, не позволяющая напрямую редактировать список дочерних узлов за пределами класса.
     */
    private final List<Node> unmodifiebleChildren = Collections.unmodifiableList(children);

    /**
     * Родительский узел. Для корневого узла будет {@code null};
     */
    private AbstractNode parent;

    /**
     * Показывает, полностью ли правильно распознан узел. Если {@code false}, то узел распознан с ошибками
     */
    protected boolean valid;

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

    public AbstractNode(AbstractNode parent) {
        this.parent = parent;
    }

    /**
     * Очищает содержимое узда перед повторным парсингом.
     */
    protected final void clear() {
        initialized = false;
        children.clear();
        valid = false;
        cachedLineBreaks = 0;
        cachedLength = 0;
        clearInternal();
    }

    /**
     * Спеифическая для типа узла логика очистки.
     */
    protected abstract void clearInternal();

    /**
     * Распознавание внутренних элементов узла.
     *
     * @param lexer Лексер для получения токенов.
     * @return Успешно ли распознавание.
     */
    public final boolean parse(Lexer lexer) {
        Token checkpoint = lexer.getPointer();
        initialized = parseInternal(lexer);
        if (!initialized) {
            lexer.setPointer(checkpoint);
            children.clear();
        }
        return initialized;
    }

    /**
     * Повторный парсинг узла при изменении кода внутри него. Очищает содержимое узда и заново вызывает {@link #parse(Lexer)}
     *
     * @param lexer Лексер с обновлённым кодом
     * @param failed Внутренний узел, который не смог распознаться из нового кода. При первом вызове {@code null}
     * @return Удачно ли прошёл повторный парсинг.
     */
    public final boolean reparse(Lexer lexer, Node failed) {
        Token start = firstToken();
        lexer.setPointer(start == null ? null : start.getPrev());
        if(parse(lexer)) {
            return true;
        } else {
            return parent != null && parent.reparse(lexer, this);
        }
    }

    /**
     * Логика распознавания, специфическая для каждого типа узла.
     *
     * @param lexer Лексер для получения токенов.
     * @return Успешно ли распознавание.
     */
    protected abstract boolean parseInternal(Lexer lexer);

    @Override
    public final List<Node> children() {
        return unmodifiebleChildren;
    }

    @Override
    public final List<Token> tokens() {
        return children.stream()
                .map(Node::tokens)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public final List<Token> tokens(TokenKind type) {
        return children.stream()
                .map(Node::tokens)
                .flatMap(Collection::stream)
                .filter(token -> token.getTokenKind().equals(type))
                .collect(Collectors.toList());
    }

    @Override
    public final List<Token> meaningTokens() {
        return children.stream()
                .map(Node::tokens)
                .flatMap(Collection::stream)
                .filter(token -> token.getTokenKind().isMeaning())
                .collect(Collectors.toList());
    }

    @Override
    public final Token firstToken() {
        if (children.isEmpty()) return null;
        return children.get(0).firstToken();
    }

    @Override
    public final Token lastToken() {
        if (children.isEmpty()) return null;
        return children.get(children.size() - 1).lastToken();
    }

    @Override
    public final AbstractNode parent() {
        return parent;
    }

    @Override
    public final void setParent(AbstractNode parent) {
        this.parent = parent;
    }

    /**
     * Добавляет узел в конец {@link #children} если это возможно.
     * @param child Добавляемый узел.
     * @throws IllegalArgumentException Если добавляемый узел пустой или если его нельзя добавить в конец {@link #children}.
     */
    public final void addChild(Node child) {
        //Если узел пустой, его нельзя добавлять.
        if (child.tokens().isEmpty()) throw new IllegalArgumentException("Can not add empty nodes");
        //Если узлов ещё не добавлено, то новый узел можно добавлять
        if (children.isEmpty()) {
            //Если узел корневой, то ему принадлежат все токены перед первым его дочерним узлом
            if (parent == null) {
                Token prev = child.firstToken().getPrev();
                for (Token token = prev; token != null; token = token.getPrev()) {
                    addChildAndUpdate(token);
                    token.setParent(this);
                }
            }
            addChildAndUpdate(child);
            return;
        }

        //Если последний токен последнего узла совпадает с первым токеном добавляемого узла, то можно добавить в конец.
        if (children.get(children.size() - 1).lastToken().getNext() == child.firstToken()) {
            addChildAndUpdate(child);
            return;
        }

        //Если последний токен последнего узла аходится перед первым токеном добавляемого узла, то можно добавить в конец, но сначала нужно добавить все токены между узлами.
        if (children.get(children.size() - 1).lastToken().isBefore(child.firstToken())) {
            List<Token> interval = lastToken().getNext().intervalTo(child.firstToken().getPrev());
            interval.forEach(this::addChildAndUpdate);
            addChildAndUpdate(child);
        } else {
            throw new IllegalArgumentException("Unable to add node to the end");
        }
    }

    /**
     * Пропускает игнорируемые токены, добавляя их в дочерние узлы
     */
    protected final void skipIgnored(Lexer lexer) {
        Token token = lexer.skipIgnored();
        if(token!=null) {
            addChild(token);
        }
    }

    /**
     * Вспомогательный метод для парсинга необязательных частей. Ставит чекпоинт для лексера, и если часть не распознана, откатывает к нему.
     * @param lexer Лексер
     * @param parseFunc Функция распознавания части узла. Возвращает {@code true} если часть распознана успешно.
     */
    protected final void parseOptional(Lexer lexer, Predicate<Lexer> parseFunc) {
        Token checkpoint = lexer.getPointer();
        if (!parseFunc.test(lexer)) {
            lexer.setPointer(checkpoint);
        }
    }

    /**
     * Использовать вместо {@code children.add(child)} для поддержания целостности.
     */
    private void addChildAndUpdate(Node child) {
        children.add(child);
        child.setParent(this);
        updateLength(cachedLength + child.length());
        updateLineBreaks(cachedLineBreaks + child.lineBreaks());
    }

    @Override
    public final int length() {
        return cachedLength;
    }

    /**
     * Обновляет сохранённую длину при изменении длины составляющих элементов
     */
    public final void updateLength() {
        int newSize = children.stream().mapToInt(Node::length).sum();
        if (newSize == cachedLength) return;
        int change = newSize - cachedLength;
        cachedLength = newSize;
        if (parent != null && parent.initialized) {
            parent.updateLength(change);
        }
    }

    private void updateLength(int change) {
        cachedLength += change;
        if (parent != null && parent.initialized) {
            parent.updateLength(change);
        }
    }

    @Override
    public final int lineBreaks() {
        return cachedLineBreaks;
    }

    public final void updateLineBreaks() {
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

    public final void update() {
        updateLength();
        updateLineBreaks();
    }

    @Override
    public final int line() {
        if (parent == null) return 0;
        if (!parent.initialized) throw new IllegalStateException("Parent not initialized yet!");
        return parent.linesBefore(this);
    }

    /**
     * Подсчитывает, сколько переносов строк было перед началом переданного элемента.
     *
     * @param child Элемент, для которого требуется найти строку начала.
     * @return Номер строки на которой начинается элемент.
     */
    public final int linesBefore(Node child) {
        if (!parent.initialized) throw new IllegalStateException("Parent not initialized yet!");
        return countUntilFoundChild(child,
                parent::linesBefore,
                Node::lineBreaks);
    }

    @Override
    public final int startPos() {
        if (parent == null) return 0;
        if (!parent.initialized) throw new IllegalStateException("Parent not initialized yet!");
        return parent.startPos(this);
    }

    public final int startPos(Node child) {
        if (!parent.initialized) throw new IllegalStateException("Parent not initialized yet!");
        return countUntilFoundChild(child,
                parent::startPos,
                Node::length);
    }

    @Override
    public final String getText() {
        return tokens().stream().map(Token::getText).collect(Collectors.joining());
    }

    @Override
    public final boolean isValid() {
        return initialized && valid;
    }

    @Override
    public final Token tokenByRelativePos(int relativePos) {
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
    public final Node minNodeIncludes(int relativeStartPos, int relativeEndPos) {
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

    protected static boolean ofType(Token token, TokenType type) {
        return token!=null && token.getTokenType() == type;
    }

    protected static boolean ofKind(Token token, TokenKind kind) {
        if(token == null) return false;
        if(token.getTokenType() == null) {
            return kind == TokenKind.IGNORED;
        }
        return token.getTokenType().getTokenKind() == kind;
    }

    @Override
    public String toString() {
        return "<" + getClass().getSimpleName() + ">" + getText();
    }
}
