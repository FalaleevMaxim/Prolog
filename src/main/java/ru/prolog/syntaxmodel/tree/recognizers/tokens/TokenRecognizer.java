package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.recognizers.NodeRecognizer;

import java.util.function.Predicate;

public abstract class TokenRecognizer implements NodeRecognizer {

    protected int matchCharacters(CharSequence code, Predicate<Character> predicate) {
        int matched;
        for (matched = 0; matched < code.length(); matched++) {
            char c = code.charAt(matched);
            if (!predicate.test(c)) break;
        }
        return matched;
    }

    protected int matchText(CharSequence code, String keyword) {
        for (int i = 0; i < keyword.length(); i++) {
            if (keyword.charAt(i) != code.charAt(i)) return 0;
        }
        return keyword.length();
    }

    protected static CharSequence subSequence(CharSequence code, int start) {
        return code.subSequence(start, code.length());
    }
}
