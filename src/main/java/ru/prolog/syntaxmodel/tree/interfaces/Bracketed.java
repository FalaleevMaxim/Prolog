package ru.prolog.syntaxmodel.tree.interfaces;

import ru.prolog.syntaxmodel.tree.Node;
import ru.prolog.syntaxmodel.tree.Token;

/**
 * Интерфейс для узлов, у которых есть открывающая и закрывающая скобки (любого вида)
 */
public interface Bracketed extends Node {
    /**
     * @return Токен открывающей скобки
     */
    Token getLb();

    /**
     * @return Токен закрывающей скобки
     */
    Token getRb();
}
