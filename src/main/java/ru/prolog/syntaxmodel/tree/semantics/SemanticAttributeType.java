package ru.prolog.syntaxmodel.tree.semantics;

/**
 * Тип семантического атрибута
 */
public enum SemanticAttributeType {
    /**
     * Значение в программе.
     * Для синтаксических конструкций, которые могут иметь разный смысл.
     * Например, f(integer) может быть объявлением предиката или объявлением функтора;
     */
    MEANING,
    /**
     * Тип данных
     */
    TYPE
}
