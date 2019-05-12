package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.recognizers.Hint;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import java.util.function.Predicate;

import static ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult.NOT_RECOGNIZED;

/**
 * Распознаёт токен-вещественное число.
 * Не распознаёт токен, если в нём нет точки. Число без точки целое, и распознавать его нужно с помощью {@link IntegerRecognizer}
 */
public class RealRecognizer extends TokenRecognizer {

    @Override
    public RecognitionResult recognize(CharSequence code) {
        if (code.length() == 0) return NOT_RECOGNIZED;
        int i = 0;
        char first = code.charAt(0);
        if (first == '+' || first == '-') i++;
        if (code.length() == i) new RecognitionResult(0);
        Predicate<Character> digitCondition = c -> c >= '0' && c <= '9';

        int beforePoint = matchCharacters(code.subSequence(i, code.length() - 1), digitCondition);
        i += beforePoint;
        //Если нет точки, то тип токена не real, а integer, поэтому токен не распознан как real.
        if (code.charAt(i) != '.') return NOT_RECOGNIZED;
        i++;
        int afterPoint = (i == code.length()) ? 0 : matchCharacters(code.subSequence(i, code.length()), digitCondition);
        if (afterPoint == 0 && beforePoint == 0) {
            return new RecognitionResult(i, true, new Hint("No digits before and after point", null));
        }
        return new RecognitionResult(i + afterPoint);
    }
}
