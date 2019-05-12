package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.recognizers.Hint;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

public abstract class AbstractKeywordRecognizer extends TokenRecognizer {
    private final String keyword;
    private final boolean isText;

    public String getKeyword() {
        return keyword;
    }

    public boolean isText() {
        return isText;
    }

    public AbstractKeywordRecognizer(String keyword) {
        this.keyword = keyword;
        isText = keyword.chars().allMatch(Character::isLetter);
    }

    @Override
    public RecognitionResult recognize(CharSequence code) {
        int matched = matchText(code, keyword);
        return matched == keyword.length() ?
                new RecognitionResult(matched) :
                new RecognitionResult(matched, true, new Hint(null, keyword));
    }
}
