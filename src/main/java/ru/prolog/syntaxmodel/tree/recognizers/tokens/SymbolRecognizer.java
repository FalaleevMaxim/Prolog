package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.recognizers.tokens.math.MathFunctionNameRecognizer;

import java.util.function.Predicate;

/**
 * Токен для значений типа symbol, имён предикатов, типов и функторов.
 */
public class SymbolRecognizer extends TokenRecognizer {
    public static final Predicate<Character> UNDERSCORE = c -> c == '_';
    public static final Predicate<Character> FIRST_CHAR = ((Predicate<Character>) Character::isLowerCase).or(UNDERSCORE);
    public static final Predicate<Character> OTHER_CHARS = ((Predicate<Character>) Character::isLetterOrDigit).or(UNDERSCORE);

    @Override
    public Token recognize(CharSequence code) {
        char first = code.charAt(0);
        if (!FIRST_CHAR.test(first)) return null;
        if (code.length() == 1) {
            if (first == '_') return null;
            return tokenOf(Character.toString(first));
        }
        int matched = 1 + matchCharacters(code.subSequence(1, code.length()), OTHER_CHARS);

        String matchedText = code.subSequence(0, matched).toString();
        boolean allUnderscores = matchedText.chars().allMatch(c -> c == '_');
        if (allUnderscores) return null;

        //Ключевые слова не могут быть именами или симвользыми данными
        if (TokenType.getKeywords().stream().anyMatch(s -> s.equalsIgnoreCase(matchedText))) {
            return null;
        }

        //Название математической функции не может быть именем или символьными данными
        if(MathFunctionNameRecognizer.FUNCTIONS.contains(matchedText.toLowerCase())) {
            return null;
        }

        return tokenOf(matchedText);
    }
}
