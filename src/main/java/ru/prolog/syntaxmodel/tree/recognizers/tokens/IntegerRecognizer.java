package ru.prolog.syntaxmodel.tree.recognizers.tokens;

import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.recognizers.Hint;

import java.util.function.Predicate;

/**
 * Распознаёт токен-целое число.
 * Знаки + или - не входят в состав числа, т.к. могут быть отдельными токенами даже находясь вплотную к цифрам.
 * Например, знаки в выражениях p(-1) и X=Y-1 имеют разный смысл. Во втором случае знак входит в состав грамматической констркции (математического выражения), а не числа.
 * Поэтому лучше сделать знак отдельным токеном, а число со знаком сделть синтаксической конструкцией.
 */
public class IntegerRecognizer extends TokenRecognizer {

    @Override
    public Token recognize(CharSequence code) {
        if (code.length() == 0) return null;
        int i = 0;
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
                return partialTokenOf(tokenText(code, i), new Hint("Missing digits after '$'", null));
            } else {
                //Если не распознано ни одной цифры, то токен не распознан совсем.
                return null;
            }
        }
        i += digitsCount;
        //Если за числом следует точка и цифра, то это число должно распознаваться как вещественное.
        boolean pointAfter = code.length() > i && code.charAt(i) == '.';
        if(pointAfter && code.length() > i+1 && Character.isDigit(code.charAt(i+1))) {
            return null;
        }

        //Но только если число не шестнадцатиричное. Шестнадцатиричным может быть только целое число.
        //Если за шестнадцатиричным числом следует точка, то шестнадцатиричное число будет распознано, а позже точка будет лишним токеном.
        pointAfter = pointAfter && !hex;
        if (pointAfter) {
            return null;
        }
        //Если цифры есть, то токен распознан.
        return tokenOf(tokenText(code, i));
    }

    private Predicate<Character> charBetween(char start, char end) {
        return c -> c >= start && c <= end;
    }
}
