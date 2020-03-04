package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.recognizers.Hint;
import ru.prolog.syntaxmodel.tree.recognizers.NodeRecognizer;

import java.util.function.Predicate;

/**
 * Предоставляет методы, которые пригодятся при распознавании токена.
 */
public abstract class TokenRecognizer implements NodeRecognizer {
    private TokenType tokenType;

    /**
     * Метод указан для уточнения возвращаемого типа.
     * {@link NodeRecognizer} распознаёт любые узлы ({@link ru.prolog.syntaxmodel.tree.Node}), а TokenRecognizer распознаёт только токены ({@link Token})
     *
     * @param code Исходный код, начиная с позиции, с которой нужно считать токен.
     * @return Распозранный токен или {@code null} если токен не распознан.
     */
    @Override
    public abstract Token recognize(CharSequence code);

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

    protected Token partialTokenOf(String text, Hint hint) {
        Token token = new Token(tokenType, text, null, true);
        token.setHint(hint);
        return token;
    }

    protected Token tokenOf(String text) {
        if(text.isEmpty()) return null;
        return new Token(tokenType, text, null, false);
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        if (this.tokenType == null) {
            this.tokenType = tokenType;
        } else {
            throw new IllegalStateException("Token type already set!");
        }
    }
}
