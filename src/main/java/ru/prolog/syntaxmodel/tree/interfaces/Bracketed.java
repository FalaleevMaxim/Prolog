package ru.prolog.syntaxmodel.tree.interfaces;

import ru.prolog.syntaxmodel.tree.Token;

/**
 * Интерфейс для узлов, у которых есть открывающая и закрывающая скобки (любого вида)
 */
public interface Bracketed {
    /**
     * @return Токен открывающей скобки
     */
    Token getLb();

    /**
     * @return Токен закрывающей скобки
     */
    Token getRb();
}
