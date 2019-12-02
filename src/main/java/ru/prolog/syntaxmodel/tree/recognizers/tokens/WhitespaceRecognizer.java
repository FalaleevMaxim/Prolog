package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.Token;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WhitespaceRecognizer extends TokenRecognizer {
    public static Set<Character> WHITESPACES = new HashSet<>(Arrays.asList(' ', '\t', '\n', '\r'));

    @Override
    public Token recognize(CharSequence code) {
        return tokenOf(tokenText(code, matchCharacters(code, WHITESPACES::contains)));
    }
}
