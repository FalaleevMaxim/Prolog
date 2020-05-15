package ru.prolog.syntaxmodel.tree;

import ru.prolog.syntaxmodel.TokenKind;
import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.recognizers.Lexer;
import ru.prolog.syntaxmodel.tree.misc.NodeError;
import ru.prolog.syntaxmodel.tree.misc.ParsingResult;
import ru.prolog.syntaxmodel.tree.semantics.SemanticInfo;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public abstract class AbstractNode implements Node {
    public static final EnumSet<TokenType> EMPTY_FOLLOW_SET = EnumSet.noneOf(TokenType.class);

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

    private final Map<Node, NodeError> errors = new HashMap<>();

    /**
     * Типы токенов, которые ожидаются дальше по ходу парсинга этого узла.
     * По мере приближения к концу парсинга follow-set будет сокращаться.
     * Токены из follow-set представляют из себя некие "чекпоинты", с которых можно продолжить парсинг при ошибках
     */
    private Set<TokenType> followSet = EnumSet.copyOf(initialFollowSet());

    /**
     * follow-set родительского узла на момент парсинга этого узла
     */
    private final Set<TokenType> parentFollowSet;

    private SemanticInfo semanticInfo;

    public AbstractNode(AbstractNode parent) {
        this.parent = parent;
        if (parent == null || parent.followSet.isEmpty()) {
            parentFollowSet = Collections.emptySet();
        } else {
            parentFollowSet = EnumSet.copyOf(parent.followSet);
        }
    }

    /**
     * Очищает содержимое узда перед повторным парсингом.
     */
    protected final void clear() {
        initialized = false;
        children.clear();
        errors.clear();
        followSet = initialFollowSet();
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
    public final ParsingResult parse(Lexer lexer) {
        Token checkpoint = lexer.getPointer();
        ParsingResult parsingResult = parseInternal(lexer);
        this.initialized = parsingResult.isOk();
        if (!this.initialized) {
            lexer.setPointer(checkpoint);
            children.clear();
        }
        return parsingResult;
    }

    /**
     * Повторный парсинг узла при изменении кода внутри него. Очищает содержимое узда и заново вызывает {@link #parse(Lexer)}
     *
     * @param lexer  Лексер с обновлённым кодом
     * @param failed Внутренний узел, который не смог распознаться из нового кода. При первом вызове {@code null}
     * @return Удачно ли прошёл повторный парсинг.
     */
    public final boolean reparse(Lexer lexer, Node failed) {
        Token start = firstToken();
        lexer.setPointer(start == null ? null : start.getPrev());
        if (parse(lexer).isOk()) {
            return true;
        } else {
            return parent != null && parent.reparse(lexer, this);
        }
    }

    protected <T extends AbstractNode> ParsingResult parseChildNode(T child, Lexer lexer, Consumer<T> fillField) {
        ParsingResult result = child.parse(lexer);
        if(result.isOk()) {
            addChild(child);
            fillField.accept(child);
        }
        return result;
    }

    protected boolean parseChildToken(Lexer lexer, Consumer<Token> fillField, TokenType... tokenTypes) {
        Token token = lexer.nextNonIgnored();
        if(ofType(token, tokenTypes)) {
            addChild(token);
            fillField.accept(token);
            return true;
        }
        return false;
    }

    /**
     * Убрать тип токена из follow-set
     *
     * @param tokenType Тип токена, который больше не ожидается
     */
    protected void dontExpect(TokenType tokenType) {
        followSet.remove(tokenType);
    }

    /**
     * Убрать типы токенов из follow-set
     *
     * @param tokenTypes Типы токенов, которые больше не ожидаются
     */
    protected void dontExpect(TokenType... tokenTypes) {
        for (TokenType tokenType : tokenTypes) {
            followSet.remove(tokenType);
        }
    }

    /**
     * Очистить follow-set.
     */
    protected void dontExpect() {
        followSet.clear();
    }

    /**
     * Собирает все follow-set всех уровней от этого узла до корня в одно множество
     *
     * @return Множество всех ожидаемых типов токенов
     */
    protected Set<TokenType> collectFollowSets() {
        Set<TokenType> all = EnumSet.noneOf(TokenType.class);
        all.addAll(followSet);
        for (AbstractNode node = this; node != null; node = node.parent) {
            all.addAll(node.parentFollowSet);
        }
        return all;
    }

    /**
     * Ищет ближайший уровень парсера, follow-set которого содержит указанный тип токена
     *
     * @param tokenType Тип токена из некоторого follow-set
     * @return ближайший уровень парсера, follow-set которого содержит указанный тип токена
     */
    protected AbstractNode nodeForFollowingToken(TokenType tokenType) {
        if(followSet.contains(tokenType)) return this;
        for (AbstractNode node = this; node.parent!=null; node = node.parent) {
            if(node.parentFollowSet.contains(tokenType)) return node.parent;
        }
        return null;
    }

    /**
     * Логика распознавания, специфическая для каждого типа узла.
     *
     * @param lexer Лексер для получения токенов.
     * @return Успешно ли распознавание.
     */
    protected abstract ParsingResult parseInternal(Lexer lexer);

    /**
     * Возвращает начальный follow-set,
     * т.е. типы токенов, которые могут встретиться в данном узле,
     * и по которым можно будет продолжить распознавание узла если где-то произойдёт ошибка
     *
     * @return Возвращает follow-set для типа узла
     */
    protected Set<TokenType> initialFollowSet() {
        return EMPTY_FOLLOW_SET;
    }

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
     *
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
        if (token != null) {
            addChild(token);
        }
    }

    /**
     * Пропускает токены, пока не найдёт токен указанного типа
     *
     * @param lexer      Лексер
     * @param tokenTypes Ожидаемые типы токенов
     * @return Токен одного из заданных типов, либо последний токен, либо {@code null} если ни одного токена не получено.
     * @see #skipUntilFollowSet(Lexer)
     */
    protected final Token skipUntil(Lexer lexer, TokenType... tokenTypes) {
        Token token = lexer.nextToken();
        while (!ofType(token, tokenTypes)) {
            Token next = lexer.nextToken();
            if (next == null) break;
            token = next;
        }
        return token;
    }

    /**
     * Пропускает токены, пока не найдёт токен из какого-либо follow-set
     *
     * @param lexer Лексер
     * @return Первый токен из follow-set либо {@code null}, если такого токена не нашлось.
     * @see #skipUntil(Lexer, TokenType...)
     * @see #collectFollowSets()
     */
    protected final Token skipUntilFollowSet(Lexer lexer) {
        Set<TokenType> followSets = collectFollowSets();
        Token token = lexer.nextNonIgnored();
        while (token != null && !followSets.contains(token.getTokenType())) {
            token = lexer.nextToken();
        }
        return token;
    }

    /**
     * Ищет узел, в follow-set которого есть заданный тип токена.
     *
     * @param type Тип токена
     * @return Узел, follow-set которого содержит данный тип токена, или {@code null} если такого нет.
     * @see #skipUntilFollowSet(Lexer)
     */
    protected final AbstractNode findFollowSetOwner(TokenType type) {
        if(followSet.contains(type)) return this;
        for(AbstractNode node = this; node.parent != null; node = node.parent) {
            if(node.parentFollowSet.contains(type)) return parent;
        }
        return null;
    }

    /**
     * Вспомогательный метод для парсинга необязательных частей. Ставит чекпоинт для лексера, и если часть не распознана, откатывает к нему.
     *
     * @param lexer     Лексер
     * @param parseFunc Функция распознавания части узла. Возвращает {@code true} если часть распознана успешно.
     */
    protected final ParsingResult parseOptional(Lexer lexer, Function<Lexer, ParsingResult> parseFunc) {
        Token checkpoint = lexer.getPointer();
        ParsingResult result = parseFunc.apply(lexer);
        if (!result.isOk()) {
            lexer.setPointer(checkpoint);
        }
        return result;
    }

    /**
     * Использовать вместо {@code children.add(child)} для поддержания целостности.
     */
    private void addChildAndUpdate(Node child) {
        children.add(child);
        child.setParent(this);
        updateLength(child.length());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public final int startPos() {
        if (parent == null) return 0;
        if (!parent.initialized) throw new IllegalStateException("Parent not initialized yet!");
        return parent.startPos(this);
    }

    /**
     * Номер символа в исходном коде, с которого начинается переданный дочерний узел
     *
     * @param child Дочерний узел этого узла
     */
    public final int startPos(Node child) {
        if(parent == null) {
            return countUntilFoundChild(child, null, Node::length);
        }
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
    public boolean isValid() {
        return initialized && errors.isEmpty();
    }

    public Map<Node, NodeError> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    /**
     * Записывает ошибку
     *
     * @param child Дочерний узел, с которым связана ошибка
     * @param after {@code true} если ошибка после узла, или {@code false} если ошибка в самом узле
     * @param text  Текст ошибки
     */
    protected final void addError(Node child, boolean after, String text) {
        errors.put(child, new NodeError(child, after, text));
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

    /**
     * Проверяет что токен имеет указанный тип
     *
     * @param token Токен
     * @param type  Ожидаемый тип
     * @return {@code true} если токен не null и имеет указанный тип.
     */
    protected static boolean ofType(Token token, TokenType type) {
        return token != null && token.getTokenType() == type;
    }

    /**
     * Проверяет что токен имеет один из указанных типов
     *
     * @param token Токен
     * @param types Ожидаемые типы
     * @return {@code true} если токен не null и имеет указанный тип.
     */
    protected static boolean ofType(Token token, TokenType... types) {
        if (token == null) return false;
        for (TokenType type : types) {
            if (token.getTokenType() == type) return true;
        }
        return false;
    }

    /**
     * Проверяет, что токен является заголовком модуля
     *
     * @param token Токен
     * @return {@code true} если токен относится к одному из типов звголовков
     * @see TokenType#HEADERS
     */
    protected static boolean isModuleHeader(Token token) {
        return token != null && TokenType.HEADERS.contains(token.getTokenType());
    }

    /**
     * Проверяет, что токен относится к заданной категшории
     *
     * @param token Токен
     * @param kind  Ожидаемая категория токена
     * @return {@code true} если токен не {@code null} и относится к заданной категории токенов
     */
    protected static boolean ofKind(Token token, TokenKind kind) {
        if (token == null) return false;
        if (token.getTokenType() == null) {
            return kind == TokenKind.IGNORED;
        }
        return token.getTokenType().getTokenKind() == kind;
    }

    @Override
    public String toString() {
        return "<" + getClass().getSimpleName() + ">" + getText();
    }

    @Override
    public SemanticInfo getSemanticInfo() {
        if(semanticInfo == null) semanticInfo = new SemanticInfo(this);
        return semanticInfo;
    }
}
