package ru.prolog.syntaxmodel.tree.interfaces;

import ru.prolog.syntaxmodel.tree.Node;
import ru.prolog.syntaxmodel.tree.Token;

import java.util.List;

/**
 * Интерфейс для узлов, имеющих разделители (точки с запятой, запятые и т.п.)
 */
public interface Separated extends Node {
    /**
     * @return Список всех разделителей внутри узла
     */
    List<Token> getSeparators();
}
