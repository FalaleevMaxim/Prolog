package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import static ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult.NOT_RECOGNIZED;

public class SingleLineCommentRecognizer extends TokenRecognizer {
    @Override
    public RecognitionResult recognize(CharSequence code) {
        if (code.length() < 2) return NOT_RECOGNIZED;
        if (code.charAt(0) == '/' && code.charAt(1) == '/') {
            return new RecognitionResult(2 + matchCharacters(subSequence(code, 2), c -> c != '\n' && c != '\r'));
        }
        return NOT_RECOGNIZED;
    }
}
