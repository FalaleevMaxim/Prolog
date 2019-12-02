package ru.prolog.syntaxmodel.tree.recognizers;

import ru.prolog.syntaxmodel.tree.Node;

public interface NodeRecognizer {
    /**
     * Распознаёт узел с начала переданной строки и возвращает количество символов в распознанном токене.
     *
     * @param code Исходный код, начиная с позиции, с которой нужно считать токен.
     * @return Распознанный узел. {@code null} если не распознан.
     */
    Node recognize(CharSequence code);
}
