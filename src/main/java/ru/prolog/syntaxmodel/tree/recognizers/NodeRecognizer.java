package ru.prolog.syntaxmodel.tree.recognizers;

public interface NodeRecognizer {
    /**
     * Распознаёт узел с начала переданной строки и возвращает количество символов в распознанном токене.
     *
     * @param code Исходный код, начиная с позиции, с которой нужно считать токен.
     * @return Распознанный узел. {@link RecognitionResult#NOT_RECOGNIZED} если не распознан.
     */
    RecognitionResult recognize(CharSequence code);
}
