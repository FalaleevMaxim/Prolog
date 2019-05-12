package ru.prolog.syntaxmodel.tree.recognizers;

public interface NodeRecognizer {
    /**
     * Распознаёт узел с начала переданной строки и возвращает количество символов в распознанном токене.
     * Если узел не распознан, возвращает 0.
     *
     * @param code Исходный код, начиная с позиции, с которой нужно считать токен.
     * @return Количество символов в распознанном токене. 0 если токен не распознан.
     */
    RecognitionResult recognize(CharSequence code);
}
