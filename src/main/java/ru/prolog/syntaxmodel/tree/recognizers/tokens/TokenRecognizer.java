package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.recognizers.NodeRecognizer;

import java.util.function.Predicate;

/**
 * Предоставляет методы, которые пригодятся при распознавании токена.
 */
public abstract class TokenRecognizer implements NodeRecognizer {

    protected int matchCharacters(CharSequence code, Predicate<Character> predicate) {
        int matched;
        for (matched = 0; matched < code.length(); matched++) {
            char c = code.charAt(matched);
            if (!predicate.test(c)) break;
        }
        return matched;
    }

    protected boolean matchText(CharSequence code, String keyword) {
        if (code.length() < keyword.length()) return false;
        for (int i = 0; i < keyword.length(); i++) {
            if (keyword.charAt(i) != code.charAt(i)) return false;
        }
        return true;
    }

    protected static CharSequence subSequence(CharSequence code, int start) {
        return code.subSequence(start, code.length());
    }

    protected static String tokenText(CharSequence code, int count) {
        return code.subSequence(0, count).toString();
    }
}
