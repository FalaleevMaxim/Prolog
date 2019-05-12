package ru.prolog.syntaxmodel.tree.recognizers;

/**
 * Результат распознавания
 */
public class RecognitionResult {
    public static RecognitionResult NOT_RECOGNIZED = new RecognitionResult(0);

    /**
     * Количество распознанных символов (0 если совсем не распознан)
     */
    public final int recognized;
    /**
     * Распознан частично (достаточно чтобы понять что это должен быть узел, но чего-то не хватает. Например, ключевое слово написано не полностью)
     */
    public final boolean partial;
    /**
     * Подсказка, как дополнить, чтобы было правильно.
     */
    public final Hint hint;

    public RecognitionResult(int recognized, boolean partial, Hint hint) {
        this.recognized = recognized;
        this.partial = partial;
        this.hint = hint;
    }

    public RecognitionResult(int recognized, boolean partial) {
        this(recognized, partial, null);
    }

    public RecognitionResult(int recognized) {
        this(recognized, false);
    }
}
