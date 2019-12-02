package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.Token;

import java.util.function.Predicate;

/**
 * Токен переменной.
 * Не включает анонимную переменную.
 *
 * @see ru.prolog.syntaxmodel.tree.recognizers.tokens.keywords.AnonymousVariableRecognizer
 */
public class VariableRecognizer extends TokenRecognizer {
    private static final Predicate<Character> FIRST_CHAR = Character::isUpperCase;
    private static final Predicate<Character> OTHER_CHARS = ((Predicate<Character>) Character::isLetterOrDigit).or(c -> c == '_');

    @Override
    public Token recognize(CharSequence code) {
        char first = code.charAt(0);
        if (!FIRST_CHAR.test(first)) return null;
        int matched = 1 + matchCharacters(code.subSequence(1, code.length()), OTHER_CHARS);
        return tokenOf(tokenText(code, matched));
    }
}
