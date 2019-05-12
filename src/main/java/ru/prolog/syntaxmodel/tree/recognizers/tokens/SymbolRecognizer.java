package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import java.util.function.Predicate;

import static ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult.NOT_RECOGNIZED;

/**
 * Токен для значений типа symbol, имён предикатов, типов и функторов.
 */
public class SymbolRecognizer extends TokenRecognizer {
    private static final Predicate<Character> UNDERSCORE = c -> c == '_';
    private static final Predicate<Character> firstChar = ((Predicate<Character>) Character::isLowerCase).or(UNDERSCORE);
    private static final Predicate<Character> otherChars = ((Predicate<Character>) Character::isLetterOrDigit).or(UNDERSCORE);

    @Override
    public RecognitionResult recognize(CharSequence code) {
        char first = code.charAt(0);
        if (!firstChar.test(first)) return NOT_RECOGNIZED;
        if (code.length() == 1) {
            if (first == '_') return NOT_RECOGNIZED;
            return new RecognitionResult(1);
        }
        int matched = 1 + matchCharacters(code.subSequence(1, code.length()), otherChars);

        boolean allUnderscores = code.subSequence(0, matched).chars().allMatch(c -> c == '_');
        if (allUnderscores) return NOT_RECOGNIZED;

        //Ключевые слова не могут быть именами или симвользыми данными
        if (TokenType.getKeywords().stream()
                .anyMatch(s -> s.equalsIgnoreCase(code.subSequence(0, matched).toString()))) {
            return NOT_RECOGNIZED;
        }

        return new RecognitionResult(matched);
    }
}
