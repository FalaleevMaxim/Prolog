package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.Token;

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
    public Token recognize(CharSequence code) {
        if (!matchText(code, PREFIX)) return null;
        return tokenOf(
                tokenText(code, PREFIX.length() + matchCharacters(
                        subSequence(code, PREFIX.length()),
                        c -> c != '\n' && c != '\r')));
    }
}
