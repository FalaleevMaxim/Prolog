package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.Token;

/**
 * Распознаватель однострочного комментария
 */
public class SingleLineCommentRecognizer extends TokenRecognizer {

    @Override
    public Token recognize(CharSequence code) {
        if (!matchText(code, "%")) return null;
        return tokenOf(
                tokenText(code, 1 + matchCharacters(
                        subSequence(code, 1),
                        c -> c != '\n' && c != '\r')));
    }
}
