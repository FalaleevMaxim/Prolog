package ru.prolog.syntaxmodel.tree.recognizers;

/**
 * Результат распознавания
 */
public class RecognitionResult {
    public static RecognitionResult NOT_RECOGNIZED = new RecognitionResult("");

    /**
     * Распознанный текст
     */
    public final String recognizedText;
    /**
     * Распознан частично (достаточно чтобы понять что это должен быть узел, но чего-то не хватает. Например, ключевое слово написано не полностью)
     */
    public final boolean partial;
    /**
     * Подсказка, как дополнить, чтобы было правильно.
     */
    public final Hint hint;

    public RecognitionResult(String recognizedText, boolean partial, Hint hint) {
        this.recognizedText = recognizedText;
        this.partial = partial;
        this.hint = hint;
    }

    public RecognitionResult(String recognizedText, boolean partial) {
        this(recognizedText, partial, null);
    }

    public RecognitionResult(String recognizedText) {
        this(recognizedText, false);
    }

    public boolean success() {
        return !recognizedText.isEmpty();
    }
}
