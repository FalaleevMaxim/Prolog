package ru.prolog.syntaxmodel.tree.interfaces;

import ru.prolog.syntaxmodel.tree.Node;
import ru.prolog.syntaxmodel.tree.Token;

/**
 * Интерфейс для узлов, имеющих имя
 */
public interface Named extends Node {
    Token getName();
}
