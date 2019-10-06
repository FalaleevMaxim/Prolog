package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import java.util.function.Predicate;

import static ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult.NOT_RECOGNIZED;

/**
 * Токен для значений типа symbol, имён предикатов, типов и функторов.
 */
public class SymbolRecognizer extends TokenRecognizer {
    public static final Predicate<Character> UNDERSCORE = c -> c == '_';
    public static final Predicate<Character> FIRST_CHAR = ((Predicate<Character>) Character::isLowerCase).or(UNDERSCORE);
    public static final Predicate<Character> OTHER_CHARS = ((Predicate<Character>) Character::isLetterOrDigit).or(UNDERSCORE);

    @Override
    public RecognitionResult recognize(CharSequence code) {
        char first = code.charAt(0);
        if (!FIRST_CHAR.test(first)) return NOT_RECOGNIZED;
        if (code.length() == 1) {
            if (first == '_') return NOT_RECOGNIZED;
            return new RecognitionResult(Character.toString(first));
        }
        int matched = 1 + matchCharacters(code.subSequence(1, code.length()), OTHER_CHARS);

        String matchedText = code.subSequence(0, matched).toString();
        boolean allUnderscores = matchedText.chars().allMatch(c -> c == '_');
        if (allUnderscores) return NOT_RECOGNIZED;

        //Ключевые слова не могут быть именами или симвользыми данными
        if (TokenType.getKeywords().stream().anyMatch(s -> s.equalsIgnoreCase(matchedText))) {
            return NOT_RECOGNIZED;
        }

        return new RecognitionResult(matchedText);
    }
}
