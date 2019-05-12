package ru.prolog.syntaxmodel.tree;

import ru.prolog.syntaxmodel.TokenKind;

import java.util.List;

public interface Node {
    /**
     * Внутренний текст узла.
     */
    String getText();

    /**
     * Полностью ли узел правильный.
     * Если false, то имеющегося кода достаточно чтобы частично распознать узел, но есть ошибки.
     */
    boolean isValid();

    /**
     * Возвращает корневой узел.
     */
    default Node root() {
        if (parent() == null) return this;
        return parent().root();
    }

    /**
     * Родительский узел. Может быть null если этот узел корневой.
     */
    AbstractNode parent();

    /**
     * Список дочерних узлов.
     */
    List<Node> children();

    /**
     * Все токены, входящие в состав элемента.
     */
    List<Token> tokens();

    /**
     * Все токены заданного типа, входящие в состав элемента.
     *
     * @param type Тип токенов.
     */
    List<Token> tokens(TokenKind type);

    /**
     * Все значащие ({@link TokenKind#isMeaning()}) токены, входящие в состав элемента.
     */
    List<Token> meaningTokens();

    void setParent(AbstractNode parent);

    /**
     * Номер символа в исхоном коде, с которого начинается этот узел.
     */
    int startPos();

    /**
     * Номер последнего символа узла в исходном коде.
     */
    default int endPos() {
        return startPos() + length();
    }

    /**
     * Длина содержимого узла в символах.
     */
    int length();

    /**
     * Номер строки в исходном коде, на которой начинается данный узел.
     */
    int line();

    /**
     * Количество переносов строк внутри узла.
     */
    int lineBreaks();

    /**
     * Возвращает токен, которому принадлежит символ с указанным номером относительно начала этого узла.
     *
     * @param relativePos Номер символа относительно {@link #startPos()}.
     * @throws StringIndexOutOfBoundsException Если {@param relativePos} выходит за пределы данного узла.
     */
    Token tokenByRelativePos(int relativePos);

    /**
     * Ищет минимальный узел, полностью содержащий заданный отрезок кода
     *
     * @param relativeStartPos Начало отрезка, относительно {@link #startPos()}.
     * @param relativeEndPos   Конец отрезка относительно {@link #startPos()}.
     * @return минимальный узел, содержащий полностью заданный отрезок.
     * @throws StringIndexOutOfBoundsException Если {@param relativeStartPos} или {@param relativeEndPos} выходит за пределы данного узла.
     */
    Node minNodeIncludes(int relativeStartPos, int relativeEndPos);
}
