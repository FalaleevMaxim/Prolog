package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.recognizers.Hint;
import ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult;

import java.util.function.Predicate;

import static ru.prolog.syntaxmodel.tree.recognizers.RecognitionResult.NOT_RECOGNIZED;

/**
 * Распознаёт токен-целое число.
 */
public class IntegerRecognizer extends TokenRecognizer {

    @Override
    public RecognitionResult recognize(CharSequence code) {
        if (code.length() == 0) return NOT_RECOGNIZED;
        int i = 0;
        char first = code.charAt(0);
        if (first == '+' || first == '-') i++;
        if (code.length() == i) new RecognitionResult(0);
        Predicate<Character> digitCondition = charBetween('0', '9');
        //После $ число в шестнадцатиричной системе.
        boolean hex = code.charAt(i) == '$';
        if (hex) {
            i++;
            digitCondition = digitCondition.or(charBetween('a', 'f')).or(charBetween('A', 'F'));
        }

        int digitsCount = i == code.length() ? 0 : matchCharacters(code.subSequence(i, code.length()), digitCondition);
        if (digitsCount == 0) {
            if (hex) {
                //Знак $ используется только в целых числах, поэтому его наличия достаточно чтобы частично определить токен.
                return new RecognitionResult(i, true, new Hint("Missing digits after '$'", null));
            } else {
                //Если не распознано ни одной цифры, то токен не распознан совсем.
                return NOT_RECOGNIZED;
            }
        }
        i += digitsCount;
        //Если за числом следует точка, то это число должно распознаваться как вещественное.
        boolean pointAfter = code.length() > i && code.charAt(i) == '.';
        //Но только если число не шестнадцатиричное. Шестнадцатиричным может быть только целое число.
        //Если за шестнадцатиричным числом следует точка, то шестнадцатиричное число будет распознано, а позже точка будет нераспознанным токеном.
        pointAfter = pointAfter && !hex;
        if (pointAfter) {
            return NOT_RECOGNIZED;
        }
        //Если цифры есть, то токен распознан.
        return new RecognitionResult(i);
    }

    private Predicate<Character> charBetween(char start, char end) {
        return c -> c >= start && c <= end;
    }
}
