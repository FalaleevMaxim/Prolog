package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import static ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult.NOT_RECOGNIZED;

/**
 * Распознаватель однострочного комментария
 */
public class SingleLineCommentRecognizer extends TokenRecognizer {
    /**
     * Начало однострочного комментария.
     * Вынесено в отдельную константу, чтобы при желании можно было поменять на "//".
     */
    private final String PREFIX = "%";

    @Override
    public RecognitionResult recognize(CharSequence code) {
        if (!matchText(code, PREFIX)) return NOT_RECOGNIZED;
        return new RecognitionResult(
                tokenText(code, PREFIX.length() + matchCharacters(
                        subSequence(code, PREFIX.length()),
                        c -> c != '\n' && c != '\r')));
    }
}
