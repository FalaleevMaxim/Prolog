package ru.prolog.syntaxmodel.tree.semantics;

import java.util.*;

/**
 * Тип семантической связи между узлами дерева
 */
public enum SemanticConnectionType {
    /**
     * Объявление
     */
    DECLARATION,
    /**
     * Реализация
     */
    IMPLEMENTATION,
    /**
     * Использование
     */
    USAGE,
    /**
     * Связь от закрывающей скобки к сообтветствующей открывающей
     */
    OPENING,
    /**
     * Связь от открывающей скобки к сообтветствующей закрывающей
     */
    CLOSING;

    private static Map<SemanticConnectionType, Set<SemanticConnectionType>> opposite = new HashMap<>();

    static {
        addOpposite(IMPLEMENTATION, DECLARATION);
        addOpposite(DECLARATION, USAGE);
        addOpposite(OPENING, CLOSING);
    }

    private static void addOpposite(SemanticConnectionType first, SemanticConnectionType second) {
        if (!opposite.containsKey(first)) opposite.put(first, new HashSet<>());
        if (!opposite.containsKey(second)) opposite.put(second, new HashSet<>());
        opposite.get(first).add(second);
        opposite.get(second).add(first);
    }


}
